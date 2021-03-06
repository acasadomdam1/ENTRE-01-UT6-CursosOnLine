import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Un objeto de esta clase mantiene una 
 * colección map que asocia  categorías (las claves) con
 * la lista (una colección ArrayList) de cursos que pertenecen a esa categoría 
 * Por ej. una entrada del map asocia la categoría 'BASES DE DATOS"' con
 * una lista de cursos de esa categoría
 * 
 * Las claves en el map se recuperan en orden alfabético y 
 * se guardan siempre en mayúsculas
 * 
 * @author Aimar Casado
 */
public class PlataformaCursos
{

    private final String ESPACIO = " ";
    private final String SEPARADOR = ":";
    private TreeMap<String, ArrayList<Curso>> plataforma;

    /**
     * Constructor  
     */
    public PlataformaCursos() 
    {

        plataforma = new TreeMap<>();

    }

    /**
     * añadir un nuevo curso al map en la categoría indicada
     * Si ya existe la categoría se añade en ella el nuevo curso
     * (al final de la lista)
     * En caso contrario se creará una nueva entrada en el map con
     * la nueva categoría y el curso que hay en ella
     * Las claves siempre se añaden en mayúsculas  
     *  
     */
    public void addCurso(String categoria, Curso curso) 
    {
        categoria = categoria.toUpperCase();

        if(!plataforma.containsKey(categoria))
        {
            ArrayList<Curso> cursos = new ArrayList<>();
            cursos.add(curso);
            plataforma.put(categoria, cursos);
        }
        else
        {
            plataforma.get(categoria).add(curso);
        }
    }

    /**
     *  Devuelve la cantidad de cursos en la categoría indicada
     *  Si no existe la categoría devuelve -1
     *
     */
    public int totalCursosEn(String categoria) 
    {
        categoria = categoria.toUpperCase();

        if(!plataforma.containsKey(categoria))
        {
            return -1;
        }

        return plataforma.get(categoria).size();
    }

    /**
     * Representación textual de la plataforma (el map), cada categoría
     * junto con el nº total de cursos que hay en ella y a continuación
     * la relación de cursos en esa categoría (ver resultados de ejecución)
     * 
     * De forma eficiente ya que habrá muchas concatenaciones
     * 
     * Usar el conjunto de entradas y un iterador
     */
    public String toString() 
    {
        StringBuilder sb = new StringBuilder("Cursos online ofrecidos por la plataforma\n\n");
        Set<Map.Entry<String, ArrayList<Curso>>> entradas = plataforma.entrySet();
        Iterator<Map.Entry<String, ArrayList<Curso>>> it = entradas.iterator();

        while(it.hasNext())
        {
            Map.Entry<String, ArrayList<Curso>> entrada = it.next();
            String categoria = entrada.getKey();
            ArrayList<Curso> cursos = entrada.getValue();

            sb.append(categoria + " (" + totalCursosEn(categoria) + ")\n");
            for(Curso curso : cursos)
            {
                sb.append(curso.toString() + "\n");
            }
        }

        return sb.toString();
    }

    /**
     * Mostrar la plataforma
     */
    public void escribir() 
    {
        System.out.println(this.toString());
    }

    /**
     *  Lee de un fichero de texto la información de los cursos
     *  En cada línea del fichero se guarda la información de un curso
     *  con el formato "categoria:nombre:fecha publicacion:nivel"
     *  
     */
    public void leerDeFichero() 
    {
        Scanner sc = new Scanner(
                this.getClass().getResourceAsStream("/cursos.csv"));
        while (sc.hasNextLine())  {
            String lineaCurso = sc.nextLine().trim();
            int p = lineaCurso.indexOf(SEPARADOR);
            String categoria = lineaCurso.substring(0, p).trim();
            Curso curso = obtenerCurso(lineaCurso.substring(p + 1));
            this.addCurso(categoria, curso);
        }

    }

    /**
     *  Dado un String con los datos de un curso
     *  obtiene y devuelve un objeto Curso
     *
     *  Ej. a partir de  "sql essential training: 3/12/2019 : principiante " 
     *  obtiene el objeto Curso correspondiente
     *  
     *  Asumimos todos los valores correctos aunque puede haber 
     *  espacios antes y después de cada dato
     */
    private Curso obtenerCurso(String lineaCurso) 
    {
        String[] spliteadaCurso = lineaCurso.split(SEPARADOR);
        String nombre = spliteadaCurso[0];

        String textoFecha = spliteadaCurso[1].trim();
        String[] spliteadaFecha = textoFecha.split("/");

        LocalDate fecha = LocalDate.of(Integer.valueOf(spliteadaFecha[2]), 
                Integer.valueOf(spliteadaFecha[1]),  Integer.valueOf(spliteadaFecha[0]));

        Nivel nivel = Nivel.valueOf(spliteadaCurso[2].toUpperCase().trim());

        return new Curso(nombre, fecha, nivel);
    }

    /**
     * devuelve un nuevo conjunto con los nombres de todas las categorías  
     *  
     */
    public TreeSet<String> obtenerCategorias() 
    {

        return new TreeSet(plataforma.keySet());

    }

    /**
     * borra de la plataforma los cursos de la categoría y nivel indicados
     * Se devuelve un conjunto (importa el orden) con los nombres de los cursos borrados 
     * 
     * Asumimos que existe la categoría
     *  
     */

    public TreeSet<String> borrarCursosDe(String categoria, Nivel nivel) 
    {
        TreeSet<String> borrados = new TreeSet<>();
        categoria = categoria.toUpperCase();
        ArrayList<Curso> cursos = plataforma.get(categoria);
        Iterator<Curso> it = cursos.iterator();

        while(it.hasNext())
        {
            Curso curso = it.next();
            if(nivel.equals(curso.getNivel()))
            {
                borrados.add(curso.getNombre());
                it.remove();
            }
        }

        return borrados;
    }

    /**
     *   Devuelve el nombre del curso más antiguo en la
     *   plataforma (el primero publicado)
     */

    public String cursoMasAntiguo() 
    {
        String nombreMasAntiguo = "";
        LocalDate fechaMasAntigua = LocalDate.MAX;
        Set<Map.Entry<String, ArrayList<Curso>>> entradas = plataforma.entrySet();
        Iterator<Map.Entry<String, ArrayList<Curso>>> it = entradas.iterator();
        while(it.hasNext()){
            Map.Entry<String, ArrayList<Curso>> entrada = it.next();
            ArrayList<Curso> cursos = entrada.getValue();
            for(Curso curso: cursos){
                if(curso.getFecha().isBefore(fechaMasAntigua)){
                    fechaMasAntigua = curso.getFecha();
                    nombreMasAntiguo = curso.getNombre();
                }
            }
        }
        return nombreMasAntiguo;
    }

    /**
     *  
     */
    public static void main(String[] args) 
    {

        PlataformaCursos plataforma = new PlataformaCursos();
        plataforma.leerDeFichero();
        plataforma.escribir();

        System.out.println(
            "Curso más antiguo: " + plataforma.cursoMasAntiguo()
            + "\n");

        String categoria = "bases de datos";
        Nivel nivel = Nivel.AVANZADO;
        System.out.println("------------------");
        System.out.println(
            "Borrando cursos de " + categoria.toUpperCase()
            + " y nivel "
            + nivel);
        TreeSet<String> borrados = plataforma.borrarCursosDe(categoria, nivel);

        System.out.println("Borrados " + " = " + borrados.toString() + "\n");
        categoria = "cms";
        nivel = Nivel.INTERMEDIO;
        System.out.println(
            "Borrando cursos de " + categoria.toUpperCase()
            + " y nivel "
            + nivel);
        borrados = plataforma.borrarCursosDe(categoria, nivel);
        System.out.println("Borrados " + " = " + borrados.toString() + "\n");
        System.out.println("------------------\n");
        System.out.println(
            "Después de borrar ....");
        plataforma.escribir();

    }
}
