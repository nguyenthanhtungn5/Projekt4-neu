package propra2.person.Controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import propra2.person.Model.Person;
import propra2.person.Model.PersonApi;
import propra2.person.Model.PersonEvent;
import propra2.person.Repository.EventRepository;
import propra2.person.Repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class PersonRestControllerTest {
    @Mock
    PersonRepository personRepository;
    @Mock
    EventRepository eventRepository;
    @InjectMocks
    PersonRestController personRestController=new PersonRestController();

    private Person person=new Person();
    private Optional<Person> firstPerson=Optional.ofNullable(person);

    private List<PersonEvent> personEventList=new ArrayList<>();
    @Before
    public void setUp() {
        person.setVorname("Tom");

        firstPerson.get().setNachname("Stark");
        firstPerson.get().setJahreslohn("10000");
        firstPerson.get().setKontakt("tung@gmail.com");
        firstPerson.get().setSkills(new String[]{"Java", "Python"});
        firstPerson.get().setId(2L);
        when(personRepository.findById(any())).thenReturn(firstPerson);

        PersonEvent personEvent =new PersonEvent();
        personEvent.setPersonId(2L);

        personEventList.add(personEvent);
        when(eventRepository.findAll()).thenReturn(personEventList);


        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

//        firstProjekt.setTitel("projekt4");
//        firstProjekt.setBeschreibung("description");
//        firstProjekt.setStartdatum("30.10.2018");
//        firstProjekt.setLaufzeit("10 Monaten");
//        firstProjekt.setId(1L);
//        firstProjekt.setTeam(new Long[]{1L});
//        when(projektRepository.findAll()).thenReturn(Arrays.asList(firstProjekt));

    }
    @Test
    public void getbyId_TEST() throws Exception {
        PersonApi actual=personRestController.getById(1L);
        PersonApi expected= new PersonApi(firstPerson.get());
        assertEquals(actual, expected);
        verify(personRepository,times(1)).findById(any());

    }
    @Test
    public void getEvents(){
        List<PersonEvent> actual= personRestController.getEvents();
        List<PersonEvent> expected=personEventList;

        assertEquals(actual, expected);
        verify(eventRepository,times(1)).deleteAll();
        verify(eventRepository,times(1)).findAll();


    }
}