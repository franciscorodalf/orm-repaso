package es.ejemplo.biblioteca.orm.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name="libros")
public class LibroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_libro")
    private Integer idLibro;
    @Column(name="titulo")
    private String titulo;
    @Column(name="isbn", nullable=false, unique=true)
    private String isbn;
    @Column(name="genero")
    private String genero;
    @Column(name="anio_publicacion")
    private Integer anioPublicacion;
    @Column(name="ejemplares")
    private Integer ejemplares;
    @Column(name="id_autor")
    private Integer idAutor;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_autor", insertable=false, updatable=false)
    private AutorEntity autor;
    
    public LibroEntity() {}
    
    public Integer getIdLibro() { return idLibro; }
    
    public void setIdLibro(Integer valor) { this.idLibro = valor; }
    
    public String getTitulo() { return titulo; }
    
    public void setTitulo(String valor) { this.titulo = valor; }
    
    public String getIsbn() { return isbn; }
    
    public void setIsbn(String valor) { this.isbn = valor; }
    
    public String getGenero() { return genero; }
    
    public void setGenero(String valor) { this.genero = valor; }
    
    public Integer getAnioPublicacion() { return anioPublicacion; }
    
    public void setAnioPublicacion(Integer valor) { this.anioPublicacion = valor; }
    
    public Integer getEjemplares() { return ejemplares; }
    
    public void setEjemplares(Integer valor) { this.ejemplares = valor; }
    
    public Integer getIdAutor() { return idAutor; }
    
    public void setIdAutor(Integer valor) { this.idAutor = valor; }
}
