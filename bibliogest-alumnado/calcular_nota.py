#!/usr/bin/env python3
"""Autoevaluación BiblioGest: Surefire, análisis estático y documentación API.

Inspirado en el calcular_nota.py aportado: bloques con pesos, resultados XML,
calificación por apartado, incidencias y una única suma final. No importa la
aplicación ni modifica los ficheros Java del alumnado.
"""
from __future__ import annotations

from collections import Counter
from dataclasses import dataclass, field
from pathlib import Path
import csv
import re
import sys
import unicodedata
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
TARGET = ROOT / 'target'
REPORTS = TARGET / 'surefire-reports'
SOURCE = ROOT / 'src/main/java/es/ejemplo/biblioteca/dao'
OUT = TARGET / 'nota'

# Las clases de prueba oficiales son la fuente de la distribución de 100 tests.
# No se utiliza la cantidad de tests detectados como denominador: los ausentes
# también cuestan puntos. Cada bloque tiene un máximo fijo.
BLOQUES = {
    'SocioDao': (12, ('SocioDaoTest',)),
    'AutorDao': (12, ('AutorDaoTest',)),
    'LibroDao': (12, ('LibroDaoTest',)),
    'PrestamoDao': (12, ('PrestamoDaoTest',)),
    'ReservaDao': (12, ('ReservaDaoTest',)),
    'Consultas SQL': (25, ('ConsultasBasicasTest', 'ConsultasJoinTest',
                           'ConsultasAgrupacionTest', 'ConsultasAvanzadasTest', 'InformesTest')),
    'Integridad': (10, ('IntegridadTest',)),
    'Transacciones': (5, ('TransaccionesTest',)),
}
assert sum(n for n, _ in BLOQUES.values()) == 100
PESOS = {'funcionalidad': 7.0, 'calidad': 2.0, 'documentacion': 1.0}
CALIDAD = {'Checkstyle': ('checkstyle-result.xml', 'error', 0.55, 0.07),
           'PMD': ('pmd.xml', 'violation', 0.90, 0.12),
           'CPD': ('cpd.xml', 'duplication', 0.55, 0.18)}

@dataclass
class Grupo:
    nombre: str
    esperados: int
    clases: tuple[str, ...]
    detectados: int = 0
    correctos: int = 0
    fallidos: int = 0
    errores: int = 0
    omitidos: int = 0
    incidencias: list[str] = field(default_factory=list)

    @property
    def nota(self) -> float:
        return PESOS['funcionalidad'] * min(self.correctos, self.esperados) / 100

    @property
    def maximo(self) -> float:
        return PESOS['funcionalidad'] * self.esperados / 100


def corto(s: str, maximo: int = 220) -> str:
    return ' '.join((s or '').split())[:maximo]


def normalizar(s: str) -> str:
    return ''.join(c for c in unicodedata.normalize('NFKD', s.lower())
                   if not unicodedata.combining(c))


def leer_tests() -> tuple[list[Grupo], list[str]]:
    grupos = [Grupo(nombre, esperados, clases) for nombre, (esperados, clases) in BLOQUES.items()]
    indices = {clase: grupo for grupo in grupos for clase in grupo.clases}
    avisos = []
    if not REPORTS.is_dir():
        return grupos, ['No existen informes JUnit en target/surefire-reports.']
    vistos = set()
    for archivo in sorted(REPORTS.glob('TEST-*.xml')):
        try:
            raiz = ET.parse(archivo).getroot()
        except ET.ParseError as ex:
            avisos.append(f'XML JUnit ilegible: {archivo.name}: {ex}')
            continue
        for caso in raiz.iter('testcase'):
            clase = (caso.get('classname') or raiz.get('name') or '').split('.')[-1]
            metodo = caso.get('name') or '(sin nombre)'
            grupo = indices.get(clase)
            if grupo is None:
                avisos.append(f'Test fuera de la rúbrica: {clase}.{metodo}')
                continue
            key = (clase, metodo)
            if key in vistos:
                avisos.append(f'Test duplicado: {clase}.{metodo}')
                continue
            vistos.add(key)
            grupo.detectados += 1
            problema = next((e for e in caso if e.tag in {'failure','error','skipped'}), None)
            if problema is None:
                grupo.correctos += 1
            else:
                if problema.tag == 'failure':
                    grupo.fallidos += 1
                elif problema.tag == 'error':
                    grupo.errores += 1
                else:
                    grupo.omitidos += 1
                grupo.incidencias.append(f'{clase}.{metodo} [{problema.tag}]: '
                                         f'{corto(problema.get("message") or problema.text or "sin detalle")}')
    for grupo in grupos:
        if grupo.detectados < grupo.esperados:
            grupo.incidencias.append(f'Faltan {grupo.esperados-grupo.detectados} tests oficiales de este grupo.')
        if grupo.detectados > grupo.esperados:
            avisos.append(f'{grupo.nombre}: {grupo.detectados} detectados > {grupo.esperados} esperados;'
                          ' revisar batería (la puntuación se limita al máximo).')
    return grupos, avisos


def leer_calidad() -> tuple[list[dict], list[str]]:
    filas, avisos = [], []
    for nombre, (fichero, etiqueta, maximo, penalizacion) in CALIDAD.items():
        ruta = TARGET / fichero
        if not ruta.is_file():
            filas.append(dict(nombre=nombre, incidencias='SIN INFORME', obtenido=0., maximo=maximo))
            avisos.append(f'{nombre}: falta {fichero}; se asignan 0 puntos al criterio.')
            continue
        try:
            raiz = ET.parse(ruta).getroot()
            problemas = [e for e in raiz.iter() if e.tag.split('}')[-1] == etiqueta]
        except ET.ParseError as ex:
            filas.append(dict(nombre=nombre, incidencias='XML INVALIDO', obtenido=0., maximo=maximo))
            avisos.append(f'{nombre}: informe XML inválido ({ex}).')
            continue
        for e in problemas:
            linea = e.get('line') or e.get('beginline') or '?'
            detalle = e.get('message') or e.get('rule') or e.get('lines') or e.text or 'incidencia'
            avisos.append(f'{nombre}, línea {linea}: {corto(detalle)}')
        filas.append(dict(nombre=nombre, incidencias=len(problemas),
                          obtenido=max(0., maximo - len(problemas) * penalizacion), maximo=maximo))
    return filas, avisos

# Reglas de documentación inspiradas en el archivo facilitado:
# comentario real, @param por parámetro y @return según el tipo.
# Las firmas son públicas y los contratos se documentan en el propio DAO.
METODO = re.compile(r'\bpublic\s+(?P<tipo>(?:[\w<>?,\[\].]+\s+)+?)'
                    r'(?P<nombre>[a-zA-Z]\w*)\s*\((?P<params>[^)]*)\)'
                    r'\s*(?:throws\s+[\w.,\s]+)?\s*\{', re.M)
DOCUMENTO = re.compile(r'/\*\*(.*?)\*/', re.S)


def documentacion_api() -> tuple[list[dict], list[str]]:
    datos = Counter()
    avisos = []
    for fichero in sorted(SOURCE.glob('*Dao.java')):
        fuente = fichero.read_text(encoding='utf-8')
        clases = list(re.finditer(r'\bpublic\s+class\s+\w+', fuente))
        for clase in clases:
            datos['clases'] += 1
            antes = fuente[:clase.start()].rstrip()
            if antes.endswith('*/') and DOCUMENTO.search(antes[antes.rfind('/**'):]):
                datos['clases_ok'] += 1
            else:
                avisos.append(f'{fichero.name}: clase sin Javadoc.')
        for m in METODO.finditer(fuente):
            nombre = m['nombre']
            if nombre == fichero.stem:  # el constructor se comprueba como método público
                pass
            datos['metodos'] += 1
            anterior = fuente[:m.start()].rstrip()
            # No aceptar Javadoc lejano separado por código fuente.
            jd = ''
            if anterior.endswith('*/'):
                inicio = anterior.rfind('/**')
                if inicio >= 0 and '*/' not in anterior[inicio+3:-2]:
                    jd = anterior[inicio:]
            descripcion = re.split(r'@(?:param|return|throws|see|since|deprecated)\b', jd, maxsplit=1)[0]
            descripcion = re.sub(r'[/\*]', ' ', descripcion).strip()
            if len(descripcion) >= 25:
                datos['metodos_ok'] += 1
            else:
                avisos.append(f'{fichero.name}.{nombre}: falta descripción Javadoc suficiente.')
            params = [p.strip().split()[-1] for p in m['params'].split(',') if p.strip()]
            for p in params:
                datos['param'] += 1
                if re.search(r'@param\s+' + re.escape(p) + r'\b\s+\S+', jd):
                    datos['param_ok'] += 1
                else:
                    avisos.append(f'{fichero.name}.{nombre}: falta @param {p}.')
            if nombre != fichero.stem and m['tipo'].strip() != 'void':
                datos['return'] += 1
                if re.search(r'@return\b\s+\S+', jd):
                    datos['return_ok'] += 1
                else:
                    avisos.append(f'{fichero.name}.{nombre}: falta @return.')
            # Las excepciones se capturan dentro de los DAO. Por tanto,
            # no exigimos @throws para operaciones que no declaran throws.
    criterios = []
    for nombre, key, maximo in [('Clases DAO documentadas','clases',.15),
                                ('Métodos públicos documentados','metodos',.35),
                                ('Etiquetas @param','param',.25),
                                ('Etiquetas @return','return',.15)]:
        total, correcto = datos[key], datos[key+'_ok']
        puntos = maximo * correcto/total if total else 0.
        criterios.append(dict(nombre=nombre, correctos=correcto,total=total,
                              obtenido=puntos,maximo=maximo))
    creado = (TARGET/'reports/apidocs/index.html').is_file() or (TARGET/'site/apidocs/index.html').is_file()
    criterios.append(dict(nombre='Generación HTML Javadoc',correctos=int(creado), total=1,
                          obtenido=.10 if creado else 0., maximo=.10))
    if not creado:
        avisos.append('No se encontró el índice HTML de Javadoc generado por Maven.')
    return criterios, avisos


def guardar_csv(nombre: str, cabeceras: list[str], filas: list[list]):
    with (OUT/nombre).open('w',encoding='utf-8',newline='') as f:
        escritor = csv.writer(f)
        escritor.writerow(cabeceras)
        escritor.writerows(filas)


def main() -> int:
    OUT.mkdir(parents=True,exist_ok=True)
    grupos, avisos = leer_tests()
    calidad, avisos_cal = leer_calidad()
    docs, avisos_doc = documentacion_api()
    correctos = sum(min(g.correctos,g.esperados) for g in grupos)
    n_func = sum(g.nota for g in grupos)
    n_cal = sum(c['obtenido'] for c in calidad)
    n_doc_pot = sum(c['obtenido'] for c in docs)
    habilitado = correctos >= 90
    n_doc = n_doc_pot if habilitado else 0.
    nota = min(10.,n_func+n_cal+n_doc)

    lineas = ['=== NOTA BIBLIOGEST POR BLOQUES ===',
              'Comando: mvn clean verify -Pnota',
              '', '=== FUNCIONALIDAD (7.00 puntos) ===']
    for g in grupos:
        lineas += [f'{g.nombre}: {g.correctos}/{g.esperados} tests; '
                   f'{g.nota:.2f}/{g.maximo:.2f} puntos']
        lineas += ['  - ' + error for error in g.incidencias]
    lineas += [f'SUBTOTAL FUNCIONALIDAD: {n_func:.2f}/7.00',
               '', '=== CALIDAD DE CÓDIGO (2.00 puntos) ===']
    for c in calidad:
        lineas.append(f'{c["nombre"]}: {c["obtenido"]:.2f}/{c["maximo"]:.2f} '
                      f'({c["incidencias"]} incidencias)')
    lineas += [f'SUBTOTAL CALIDAD: {n_cal:.2f}/2.00',
               '', '=== DOCUMENTACIÓN API (1.00 punto) ===']
    for d in docs:
        lineas.append(f'{d["nombre"]}: {d["correctos"]}/{d["total"]} '
                      f'({d["obtenido"]:.2f}/{d["maximo"]:.2f})')
    lineas.append(f'Umbral 90/100: {correctos}/100; '
                  f'{"HABILITADA" if habilitado else "BLOQUEADA"}; '
                  f'{n_doc:.2f}/1.00 aplicados (potencial {n_doc_pot:.2f})')
    lineas += ['', '=== SUMA TOTAL ===',
               f'Funcionalidad: {n_func:.2f}/7.00',
               f'Calidad:       {n_cal:.2f}/2.00',
               f'Documentación: {n_doc:.2f}/1.00',
               f'NOTA FINAL:   {nota:.2f}/10.00']
    avisos += avisos_cal + avisos_doc
    if avisos:
        lineas += ['', '=== INCIDENCIAS DE CALIDAD, JAVADOC Y EVALUACIÓN ===']
        lineas += ['- ' + aviso for aviso in avisos]
    (TARGET/'notas_por_bloques.txt').write_text('\n'.join(lineas)+'\n', encoding='utf-8')
    md = ['# Rúbrica de autoevaluación BiblioGest', '',
          '## Funcionalidad por DAO y consultas', '',
          '| Bloque | Superados | Fallidos | Errores | Omitidos | Puntos | Máximo |',
          '|---|---:|---:|---:|---:|---:|---:|']
    for g in grupos:
        md.append(f'| {g.nombre} | {g.correctos}/{g.esperados} | {g.fallidos} | {g.errores} | '
                  f'{g.omitidos} | {g.nota:.2f} | {g.maximo:.2f} |')
    md += ['', '## Calidad', '', '| Herramienta | Incidencias | Puntos | Máximo |',
           '|---|---:|---:|---:|']
    for c in calidad:
        md.append(f'| {c["nombre"]} | {c["incidencias"]} | {c["obtenido"]:.2f} | {c["maximo"]:.2f} |')
    md += ['', '## Documentación API', '', '| Criterio | Acreditados | Puntos | Máximo |',
           '|---|---:|---:|---:|']
    for d in docs:
        md.append(f'| {d["nombre"]} | {d["correctos"]}/{d["total"]} | '
                  f'{d["obtenido"]:.2f} | {d["maximo"]:.2f} |')
    md += ['', f'**Condición:** {correctos}/100 tests. '
           f'Javadoc {"habilitado" if habilitado else "bloqueado (<90 tests)"}.',
           '', '## Suma final', '',
           '| Apartado | Puntos | Máximo |','|---|---:|---:|',
           f'| Funcionalidad | {n_func:.2f} | 7.00 |',
           f'| Calidad | {n_cal:.2f} | 2.00 |',
           f'| Javadoc aplicado | {n_doc:.2f} | 1.00 |',
           f'| **NOTA FINAL** | **{nota:.2f}** | **10.00** |',
           '', '## Tests fallidos por bloque', '']
    for g in grupos:
        md += [f'### {g.nombre}', '']
        md += ['- ' + err for err in g.incidencias] or ['Sin incidencias detectadas.']
        md.append('')
    md += ['## Incidencias de calidad y documentación','']
    md += ['- ' + aviso for aviso in avisos] or ['Sin incidencias detectadas.']
    (OUT/'informe-nota.md').write_text('\n'.join(md)+'\n',encoding='utf-8')
    guardar_csv('desglose-dao.csv', ['grupo','esperados','detectados','pasados','fallidos','errores','omitidos','obtenido','maximo'],
                [[g.nombre,g.esperados,g.detectados,g.correctos,g.fallidos,g.errores,g.omitidos,
                  f'{g.nota:.4f}',f'{g.maximo:.4f}'] for g in grupos])
    guardar_csv('rubrica-completa.csv',['apartado','criterio','obtenido','maximo','detalle'],
                [['Funcionalidad',g.nombre,f'{g.nota:.4f}',f'{g.maximo:.4f}',f'{g.correctos}/{g.esperados}'] for g in grupos]
                + [['Calidad',c['nombre'],f'{c["obtenido"]:.4f}',f'{c["maximo"]:.4f}',c['incidencias']] for c in calidad]
                + [['Javadoc',d['nombre'],f'{d["obtenido"]:.4f}',f'{d["maximo"]:.4f}',f'{d["correctos"]}/{d["total"]}'] for d in docs])
    guardar_csv('nota.csv',['tests_correctos','nota_funcional','nota_calidad','nota_javadoc_potencial',
                             'javadoc_habilitado','nota_javadoc_aplicada','nota_final'],
                [[correctos,f'{n_func:.4f}',f'{n_cal:.4f}',f'{n_doc_pot:.4f}',habilitado,f'{n_doc:.4f}',f'{nota:.4f}']])
    print('\n'.join(lineas), flush=True)
    print('\nInforme: target/nota/informe-nota.md')
    print('\n' + '=' * 62)
    print(f'  NOTA FINAL BIBLIOGEST: {nota:.2f} / 10.00')
    print(f'  Tests: {correctos}/100 | Funcionalidad: {n_func:.2f}/7.00')
    print(f'  Calidad: {n_cal:.2f}/2.00 | Javadoc: {n_doc:.2f}/1.00')
    print(f'  Documentación: {"HABILITADA" if habilitado else "BLOQUEADA (menos de 90 tests)"}')
    print('=' * 62, flush=True)
    return 0

if __name__ == '__main__':
    sys.exit(main())
