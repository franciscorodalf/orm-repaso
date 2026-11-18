#!/usr/bin/env python3
"""SportRent: weighted Surefire reports + DAO source quality + Javadoc threshold.
Execute after: mvn clean verify -Pnota. Missing tests score zero.
"""
from pathlib import Path
import re, xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parent; TARGET=ROOT/'target'; REPORTS=TARGET/'surefire-reports'
SRC=ROOT/'src/main/java/es/examen/sportrent/dao'
BLOCKS=[('A01ClienteDaoTest',20,1.40),('A02EquipoDaoTest',20,1.40),('A03AlquilerDaoTest',25,1.70),('B01ConsultasBasicasTest',12,.75),('B02ConsultasIntermediasTest',20,1.25),('B03ConsultasAvanzadasTest',23,1.50)]
EXPECTED_TESTS = {'A01ClienteDaoTest': ['verificarListadoOrdenado', 'verificarTipoElementosListado', 'verificarBusquedaRegistroExistente', 'verificarBusquedaRegistroInexistente', 'verificarInsercionRegistro', 'verificarInsercionRegistroDuplicado', 'verificarActualizacionRegistro', 'verificarActualizacionRegistroInexistente', 'verificarEliminacionRegistro', 'verificarEliminacionRegistroInexistente', 'verificarListadoSinRegistros', 'verificarBusquedaConIdentificadorNulo', 'verificarInsercionConModeloNulo', 'verificarActualizacionConModeloNulo', 'verificarEliminacionConIdentificadorNulo', 'verificarIdentificadorVacio', 'verificarIdentificadorEnInsercion', 'verificarValidacionAtributos', 'verificarIntegridadReferencial', 'verificarConservacionDatosTrasDuplicado'], 'A02EquipoDaoTest': ['verificarListadoOrdenado', 'verificarTipoElementosListado', 'verificarBusquedaRegistroExistente', 'verificarBusquedaRegistroInexistente', 'verificarInsercionRegistro', 'verificarInsercionRegistroDuplicado', 'verificarActualizacionRegistro', 'verificarActualizacionRegistroInexistente', 'verificarEliminacionRegistro', 'verificarEliminacionRegistroInexistente', 'verificarListadoSinRegistros', 'verificarBusquedaConIdentificadorNulo', 'verificarInsercionConModeloNulo', 'verificarActualizacionConModeloNulo', 'verificarEliminacionConIdentificadorNulo', 'verificarIdentificadorVacio', 'verificarIdentificadorEnInsercion', 'verificarValidacionAtributos', 'verificarIntegridadReferencial', 'verificarConservacionDatosTrasDuplicado'], 'A03AlquilerDaoTest': ['verificarListadoOrdenado', 'verificarTipoElementosListado', 'verificarBusquedaRegistroExistente', 'verificarBusquedaRegistroInexistente', 'verificarInsercionRegistro', 'verificarDisponibilidadEnInsercion', 'verificarActualizacionRegistro', 'verificarActualizacionRegistroInexistente', 'verificarEliminacionRegistro', 'verificarEliminacionRegistroInexistente', 'verificarListadoSinRegistros', 'verificarBusquedaConIdentificadorNulo', 'verificarInsercionConModeloNulo', 'verificarActualizacionConModeloNulo', 'verificarEliminacionConIdentificadorNulo', 'verificarIntervaloDeFechas', 'verificarClienteRelacionado', 'verificarEquipoRelacionado', 'verificarRelacionEquipoDevuelto', 'verificarPersistenciaFechas', 'verificarActualizacionConRelacionInvalida', 'verificarInsercionConRelacionesInvalidas', 'verificarIndependenciaEliminacion', 'verificarListadoEnNuevaSesion', 'verificarIntegridadTrasInsercionRechazada'], 'B01ConsultasBasicasTest': ['verificarResultadoAlquileresPorCliente', 'verificarOrdenacionAlquileresPorCliente', 'verificarImplementacionAlquileresPorCliente', 'verificarResultadoVacioAlquileresPorCliente', 'verificarResultadoCincoEquiposMasCaros', 'verificarOrdenacionCincoEquiposMasCaros', 'verificarImplementacionCincoEquiposMasCaros', 'verificarResultadoVacioCincoEquiposMasCaros', 'verificarResultadoAlquileresPendientes', 'verificarOrdenacionAlquileresPendientes', 'verificarImplementacionAlquileresPendientes', 'verificarResultadoVacioAlquileresPendientes'], 'B02ConsultasIntermediasTest': ['verificarResultadoClientesConMasDeDosAlquileres', 'verificarOrdenacionClientesConMasDeDosAlquileres', 'verificarImplementacionClientesConMasDeDosAlquileres', 'verificarResultadoVacioClientesConMasDeDosAlquileres', 'verificarCantidadPrimerClienteClientesConMasDeDosAlquileres', 'verificarResultadoMaximoPrecioPorCategoria', 'verificarOrdenacionMaximoPrecioPorCategoria', 'verificarImplementacionMaximoPrecioPorCategoria', 'verificarResultadoVacioMaximoPrecioPorCategoria', 'verificarSegundaCategoriaMaximoPrecioPorCategoria', 'verificarResultadoEquiposAlquiladosDosVeces', 'verificarOrdenacionEquiposAlquiladosDosVeces', 'verificarImplementacionEquiposAlquiladosDosVeces', 'verificarResultadoVacioEquiposAlquiladosDosVeces', 'verificarCantidadPrimerEquipoEquiposAlquiladosDosVeces', 'verificarResultadoTresEquiposMasIngresos', 'verificarOrdenacionTresEquiposMasIngresos', 'verificarImplementacionTresEquiposMasIngresos', 'verificarResultadoVacioTresEquiposMasIngresos', 'verificarIngresosPrimerEquipoTresEquiposMasIngresos'], 'B03ConsultasAvanzadasTest': ['verificarResultadoClientesSuperanReferencia', 'verificarOrdenacionClientesSuperanReferencia', 'verificarImplementacionClientesSuperanReferencia', 'verificarResultadoVacioClientesSuperanReferencia', 'verificarSegundoClienteClientesSuperanReferencia', 'verificarCantidadPrimerClienteClientesSuperanReferencia', 'verificarCantidadSegundoClienteClientesSuperanReferencia', 'verificarReferenciaInexistenteClientesSuperanReferencia', 'verificarReferenciaNulaClientesSuperanReferencia', 'verificarReferenciaConUnAlquilerClientesSuperanReferencia', 'verificarReferenciaSinAlquileresClientesSuperanReferencia', 'verificarReferenciaConMayorCantidadClientesSuperanReferencia', 'verificarResultadoEquiposSobreMediaCategoria', 'verificarOrdenacionEquiposSobreMediaCategoria', 'verificarImplementacionEquiposSobreMediaCategoria', 'verificarResultadoVacioEquiposSobreMediaCategoria', 'verificarSegundoEquipoEquiposSobreMediaCategoria', 'verificarTercerEquipoEquiposSobreMediaCategoria', 'verificarComparacionDePreciosEquiposSobreMediaCategoria', 'verificarCategoriaPrimerEquipoEquiposSobreMediaCategoria', 'verificarMediasInformadasEquiposSobreMediaCategoria', 'verificarPrecioPrimerEquipoEquiposSobreMediaCategoria', 'verificarResultadoEstableEquiposSobreMediaCategoria']}

def success_tests():
    found={};issues=[]
    for file in REPORTS.glob('TEST-*.xml'):
        try:
            for item in ET.parse(file).getroot().iter('testcase'):
                cls=(item.attrib.get('classname') or '').split('.')[-1]
                name=item.attrib.get('name','')
                key=(cls,name)
                if key in found:
                    issues.append('Prueba repetida: '+str(key))
                    continue
                found[key]=not any(e.tag in ('failure','error','skipped') for e in item)
                if not found[key]:issues.append(f'{cls}.{name} fallida')
        except (OSError,ET.ParseError) as err:
            issues.append(f'XML inválido: {file.name} {err}')
    rows=[]
    for name,count,points in BLOCKS:
        expected=EXPECTED_TESTS[name]
        assert len(expected)==count, f'Número incorrecto de pruebas oficiales: {name}'
        passed=sum(found.get((name,method),False) for method in expected)
        rows.append((name,passed,count,round(points*passed/count,4),points))
    return rows,issues

def read_report(path,tag):
    if not path.is_file():return None
    try:return sum(1 for e in ET.parse(path).getroot().iter() if e.tag.split('}')[-1]==tag)
    except ET.ParseError:return None

def quality():
    # Each tool is zero when its report is absent; penalties are bounded per tool.
    cs=read_report(TARGET/'checkstyle-result.xml','error')
    pm=read_report(TARGET/'pmd.xml','violation')
    cp=read_report(TARGET/'cpd.xml','duplication')
    scores=[('Checkstyle',cs,max(0,.30-.03*cs) if cs is not None else 0,.30),
            ('PMD',pm,max(0,.45-.05*pm) if pm is not None else 0,.45),
            ('CPD',cp,max(0,.25-.08*cp) if cp is not None else 0,.25)]
    return scores

def javadoc():
    files=[SRC/(x+'Dao.java') for x in ('Cliente','Equipo','Alquiler')]
    summary=[];counts=[0,0,0,0];total=[3,24,0,0]
    for f in files:
        txt=f.read_text(encoding='utf8') if f.is_file() else ''
        # Class docs should precede class declaration directly.
        if re.search(r'/\*\*[^*].*?\*/\s*public\s+class\s+\w+Dao',txt,re.S):counts[0]+=1
        matches=list(re.finditer(r'(?P<doc>/\*\*.*?\*/\s*)?public\s+(?:List<[^>]+>|boolean|Cliente|Equipo|Alquiler)\s+\w+\s*\([^)]*\)\s*\{',txt,re.S))
        for m in matches:
            doc=m.group('doc') or ''
            if len(re.sub(r'@\w+[^\n]*','',re.sub(r'[/*]',' ',doc)).strip())>=20:counts[1]+=1
            sig=m.group(0);params=re.search(r'\((.*?)\)',sig,re.S).group(1)
            for pair in params.split(','):
                if not pair.strip():continue
                total[2]+=1
                if re.search(r'@param\s+'+re.escape(pair.split()[-1])+r'\b\s+\S+',doc):counts[2]+=1
            total[3]+=1
            if re.search(r'@return\s+\S+',doc):counts[3]+=1
    vals=[.15*counts[0]/total[0],.35*counts[1]/total[1],.25*counts[2]/total[2] if total[2] else 0,.15*counts[3]/total[3]]
    html=(TARGET/'reports/apidocs/index.html').exists() or (TARGET/'site/apidocs/index.html').exists()
    return min(1,sum(vals)+(.10 if html else 0)),list(zip(('clases','métodos','@param','@return'),counts,total)),html

def jpql_audit():
    errors=[]
    for name in ('Cliente','Equipo','Alquiler'):
        f=SRC/(name+'Dao.java');source=f.read_text(encoding='utf8') if f.exists() else ''
        for forbidden in ('createNativeQuery(', 'DriverManager.getConnection(', 'prepareStatement('):
            if forbidden in source:errors.append(f'{name}Dao: prohibido {forbidden}')
        if 'createQuery(' not in source:errors.append(f'{name}Dao: no se detecta JPQL')
    return errors

def main():
    rows,problems=success_tests();problems+=jpql_audit()
    q=quality();j,detail,html=javadoc()
    functional=sum(r[3] for r in rows)
    # Una entrega sin ninguna prueba funcional superada debe empezar en 0.00.
    # No se regalan puntos de estilo por código base o DAO sin implementar.
    passed_total = sum(row[1] for row in rows)
    quality_score = sum(x[2] for x in q) if passed_total > 0 else 0.0
    unlocked=functional>=6.40-1e-9
    applied=j if unlocked else 0
    total=round(functional+quality_score+applied,4)
    lines=['# SportRent — calificación automática','', '## Funcionalidad (8 puntos)',
           '| Grupo | Aciertos | Puntos | Máximo |','|---|---:|---:|---:|']
    for n,passed,count,points,maxp in rows:lines.append(f'| {n} | {passed}/{count} | {points:.3f} | {maxp:.2f} |')
    lines+=['',f'**Funcionalidad: {functional:.3f}/8.00**','', '## Calidad (1 punto)',
            '| Herramienta | Incidencias | Nota |','|---|---:|---:|']
    for n,issues,s,m in q:lines.append(f'| {n} | {issues if issues is not None else "SIN INFORME"} | {s:.3f}/{m:.2f} |')
    lines+=['',f'**Calidad: {quality_score:.3f}/1.00**', '', '## Javadoc (1 punto; umbral 6.40/8)',
            f'Potencial: {j:.3f}; umbral: {"SUPERADO" if unlocked else "NO SUPERADO"}; aplicado: {applied:.3f}',
            f'Javadoc HTML: {"sí" if html else "no"}', '']
    for key,good,all_ in detail:lines.append(f'- {key}: {good}/{all_}')
    lines+=['',f'## NOTA FINAL: {total:.2f}/10', '', '## Incidencias']
    lines += ['- '+p for p in problems] if problems else ['Sin incidencias detectadas.']
    out=TARGET/'nota';out.mkdir(parents=True,exist_ok=True)
    (out/'informe-nota.md').write_text('\n'.join(lines)+'\n',encoding='utf8')
    (TARGET/'notas_por_bloques.txt').write_text('\n'.join(lines)+'\n',encoding='utf8')
    print('\n'.join(lines))
if __name__=='__main__':main()
