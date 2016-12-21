package cl.ucn.disc.isof.fivet.domain.service.ebean;

import cl.ucn.disc.isof.fivet.domain.model.Control;
import cl.ucn.disc.isof.fivet.domain.model.Paciente;
import cl.ucn.disc.isof.fivet.domain.model.Persona;
import cl.ucn.disc.isof.fivet.domain.service.BackendService;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase de testing del {@link BackendService}.
 */
@Slf4j
@FixMethodOrder(MethodSorters.DEFAULT)
public class TestEbeanBackendService {

    /**
     * Todos los test deben terminar antes de 60 segundos.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    /**
     * Configuracion de la base de datos:  h2, hsql, sqlite
     * WARN: hsql no soporta ENCRYPT
     */
    private static final String DB = "h2";

    /**
     * Backend
     */
    private BackendService backendService;

    /**
     * Cronometro
     */
    private Stopwatch stopWatch;

    /**
     * Antes de cada test
     */
    @Before
    public void beforeTest() {

        stopWatch = Stopwatch.createStarted();
        log.debug("Initializing Test Suite with database: {}", DB);

        backendService = new EbeanBackendService(DB);
        backendService.initialize();
    }

    /**
     * Despues del test
     */
    @After
    public void afterTest() {

        log.debug("Test Suite done. Shutting down the database ..");
        backendService.shutdown();

        log.debug("Test finished in {}", stopWatch.toString());
    }

    /**
     * Test de la persona
     */
    @Test
    public void testPersona() {

        final String rut = "1-1";
        final String nombre = "Este es mi nombre";
        final String mail = "jcc@gmail.com";
        // Insert into backend
        {
            final Persona persona = Persona.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .login("jquiñones")
                    .password("jquiñones1235")
                    .tipo(Persona.Tipo.CLIENTE)
                    .direccion("Angamos 061")
                    .mail(mail)
                    .build();

           persona.insert();

            log.debug("Persona to insert: {}", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
        }

        // Get from backend v1
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
            Assert.assertEquals(nombre, persona.getNombre());
            Assert.assertNotNull("Pacientes null", persona.getPacientes());
            Assert.assertTrue("Pacientes != 0", persona.getPacientes().size() == 0);

            // Update nombre
            persona.setNombre(nombre);
            persona.update();
        }

        // Get from backend v2
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertEquals(nombre, persona.getNombre());
        }

        // Get from backend v3
        {
            final Persona persona = backendService.getPersona(mail);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertEquals(rut, persona.getRut());
            Assert.assertEquals(nombre, persona.getNombre());
            Assert.assertEquals(mail, persona.getMail());
        }

    }

    @Test
    public void testGetPacientesAndGetPaciente() throws ParseException {

        final int numero = 1;
        final int numero2 = 2;
        final SimpleDateFormat formato = new SimpleDateFormat("dd-mm-yyyy");
        // Insert into backend
        {
            String fecha = "21-10-2010";
            Date date = new Date();
            try {
                date = formato.parse(fecha);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final Paciente paciente = Paciente.builder()
                    .numero(numero)
                    .nombre("spike")
                    .especie("perro")
                    .fechaNacimiento(date)
                    .raza("bulldog")
                    .sexo(Paciente.Sexo.MACHO)
                    .color("cafe")
                    .build();

            paciente.insert();
            log.debug("Paciente to insert: {}", paciente);
            Assert.assertNotNull("Objeto sin id", paciente.getId());

            try {
                date = formato.parse("25-05-2013");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final Paciente paciente1 = Paciente.builder()
                    .numero(numero2)
                    .nombre("misifus")
                    .especie("gato")
                    .fechaNacimiento(date)
                    .raza("siames")
                    .sexo(Paciente.Sexo.HEMBRA)
                    .color("blanco")
                    .build();

            paciente1.insert();
            log.debug("Paciente to insert: {}", paciente1);
            Assert.assertNotNull("Objeto sin id", paciente1.getId());
            Assert.assertNotNull("Paciente con nombre", paciente1.getNombre());
        }

        // Get from backend v1
        {
            final Paciente paciente = backendService.getPaciente(numero);
            log.debug("Paciente founded: {}", paciente);
            Assert.assertNotNull("Can't find Paciente", paciente);
            Assert.assertNotNull("Objeto sin id", paciente.getId());
            Assert.assertNotNull("Paciente sin numero de ficha", paciente.getNumero());
            Assert.assertTrue(numero == paciente.getNumero());
        }

        // Get from backend v2
        {
            final List<Paciente> pacientes = backendService.getPacientes();
            Assert.assertNotNull("Can't find Pacientes", pacientes);
            //Assert.assertTrue("Solo hay dos pacientes",2 == pacientes.size());
            for (Paciente p : pacientes) {
                log.debug("Paciente founded: {}", p);
                Assert.assertNotNull("Objeto sin id", p.getId());
            }


        }

    }

    @Test
    public void testGetControlesVeterinario() {
        final String rut = "1-2";
        final String nombre = "veterinario1";
        final String mail = "jcq@gmail.com";
        final int codigo = 1;
        final int codigo1 = 2;
        // Insert into backend
        {
            final Persona vet = Persona.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .login("jquiñones")
                    .password("jquiñones1236")
                    .tipo(Persona.Tipo.VETERINARIO)
                    .direccion("Angamos 061")
                    .mail(mail)
                    .build();
            vet.insert();

            Date fecha = new Date(2013,10,21);
            Date fecha1 = new Date(2015,05,05);

            final Control control = Control.builder()
                    .fecha(fecha)
                    .codigo(codigo)
                    .altura(1.5)
                    .diagnostico("enfermo")
                    .veterinario(vet)
                    .build();
            control.insert();

            final Control control1 = Control.builder()
                    .fecha(fecha1)
                    .codigo(codigo1)
                    .altura(0.5)
                    .diagnostico("gripe")
                    .veterinario(vet)
                    .build();
            control1.insert();
        }

        // Get from backend v1
        {
            final Persona persona = backendService.getPersona(rut);
            log.debug("Persona founded: {}", persona);
            Assert.assertNotNull("Can't find Persona", persona);
            Assert.assertNotNull("Objeto sin id", persona.getId());
            Assert.assertEquals(nombre, persona.getNombre());
            Assert.assertNotNull("Pacientes null", persona.getPacientes());
            Assert.assertTrue("Pacientes != 0", persona.getPacientes().size() == 0);

        }

        List<Control> controles = backendService.getControlesVeterinario(rut);
        for(Control c : controles){
            log.debug("Controles founded: {}", c);
        }
    }

    @Test
    public void testGetPacientesPorNombre() {
        final int numero = 3;
        final int numero1 = 4;
        final String nomb1 = "pep";
        final String nomb2 = "pepito";
        // Insert into backend
        {
            final Paciente paciente = Paciente.builder()
                    .numero(numero)
                    .nombre(nomb1)
                    .especie("perro")
                    .raza("bulldog")
                    .sexo(Paciente.Sexo.MACHO)
                    .color("cafe")
                    .build();

            paciente.insert();

            final Paciente paciente1 = Paciente.builder()
                    .numero(numero1)
                    .nombre(nomb2)
                    .especie("gato")
                    .raza("siames")
                    .sexo(Paciente.Sexo.HEMBRA)
                    .color("blanco")
                    .build();

            paciente1.insert();
        }

        // Get from backend v1
        {
            final List<Paciente> pacientes = backendService.getPacientesPorNombre("pep");
            log.debug("List Paciente founded: {}");
            Assert.assertNotNull("Can't find Pacientes", pacientes);
            //Al ejecutar este test hay
            log.debug("List Paciente founded: {} " + pacientes.size());
            Assert.assertTrue("Solo hay dos pacientes",2 == pacientes.size());
            for (Paciente p : pacientes) {
                log.debug("Paciente founded: {}", p);
                Assert.assertNotNull("Objeto sin id", p.getId());
                Assert.assertNotNull("Objeto sin nombre", p.getNombre());
            }


        }
    }

    @Test
    public void testAgregarControl() {

        final String rut = "1-3";
        final String nombre = "veterinario1";
        final String mail = "jqm@gmail.com";
        final int codigo = 3;
        final int numero = 5;

        // Insert into backend
        {
            final Persona vet = Persona.builder()
                    .nombre(nombre)
                    .rut(rut)
                    .login("jquiñones")
                    .password("jquiñones1234")
                    .tipo(Persona.Tipo.VETERINARIO)
                    .direccion("Angamos 061")
                    .mail(mail)
                    .build();
            vet.insert();

            Date fecha = new Date(2013, 10, 21);

            final Control control = Control.builder()
                    .fecha(fecha)
                    .codigo(codigo)
                    .altura(1.5)
                    .diagnostico("enfermo")
                    .veterinario(vet)
                    .build();
            control.insert();

            final Paciente paciente = Paciente.builder()
                    .numero(numero)
                    .especie("perro")
                    .fechaNacimiento(fecha)
                    .raza("bulldog")
                    .sexo(Paciente.Sexo.MACHO)
                    .color("cafe")
                    .build();
            paciente.insert();


            backendService.agregarControl(control,numero);
        }
    }

    @Test
    public void allTest() throws ParseException {
        this.testAgregarControl();
        this.testGetControlesVeterinario();
        this.testGetPacientesAndGetPaciente();
        this.testGetPacientesPorNombre();
        this.testPersona();
    }
}
