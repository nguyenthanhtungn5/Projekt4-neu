package propra2.person;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import propra2.person.Controller.PersonController;
import propra2.person.Model.Person;
import propra2.person.Model.Projekt;
import propra2.person.Repository.PersonRepository;
import propra2.person.Repository.ProjektRepository;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration

public class PersonControllerJunitTests {

	private Person firstPerson = new Person("Tom","Stark","10000",
			"tung@gmail.com",new String[]{"Java","Python"}, new Long[]{1L});
	private Projekt firstProjekt = new Projekt();

	private MockMvc mockMvc;
	@Mock
    PersonRepository personRepository;
	@Mock
    ProjektRepository projektRepository;
	@Before
	public void setUp(){
		//Person erzeugen

		firstPerson.setVorname("Tom");
		firstPerson.setNachname("Stark");
		firstPerson.setJahreslohn("10000");
		firstPerson.setKontakt("tung@gmail.com");
		firstPerson.setSkills(new String[]{"Java","Python"});
		firstPerson.setProjekteId(new Long[]{1L, 2L});
		// when
		when(personRepository.findAll()).thenReturn(Arrays.asList(firstPerson));
		when(personRepository.findById(	1L)).thenReturn(java.util.Optional.ofNullable(firstPerson));
		when(personRepository.save(Mockito.isA(Person.class))).thenReturn(firstPerson);


		// wichtig für get url
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/jsp/view/");
		viewResolver.setSuffix(".jsp");
		// Projekt erzeugen

		firstProjekt.setTitel("projekt4");
		firstProjekt.setBeschreibung("description");
		firstProjekt.setStartdatum("30.10.2018");
		firstProjekt.setLaufzeit("10 Monaten");
		//when
		when(projektRepository.findAll()).thenReturn(Arrays.asList(firstProjekt));
//		when(projektRepository.deleteById(1L)).thenReturn(projektDummyREPO=null);
//		when(projektRepository.save(any(Projekt.class))).thenReturn(projektDummyREPO.add(firstProjekt));

		// build mockmvc
		this.mockMvc = MockMvcBuilders.standaloneSetup(new PersonController(projektRepository,personRepository))
				.setViewResolvers(viewResolver)
				.build();
	}

	@Test
	public void AddPersonPageTEST() throws Exception {
		mockMvc.perform(get("/addPerson"))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(view().name("addPerson"))
				.andExpect(model().attribute("projekte",hasSize(1)))
				.andExpect(model().attribute("projekte", hasItem(
						allOf(
								hasProperty("titel",is("projekt4")),
								hasProperty("beschreibung",is("description")),
								hasProperty("startdatum",is("30.10.2018")),
								hasProperty("laufzeit",is("10 Monaten"))
						)
				)));
		verify(projektRepository, times(1)).findAll();
		verifyNoMoreInteractions(projektRepository);
	}

	@Test
	public void AddToDatabaseTEST() throws Exception {
		when(projektRepository.findAllById(1L)).thenReturn(firstProjekt);
		//when(personRepository.save(Mockito.isA(Person.class))).th(firstPerson);
		personRepository.save(firstPerson);
		mockMvc.perform(get("/add")

							.param("vorname","Tom")
							.param("nachname","Stark")
							.param("jahreslohn","10000")
							.param("kontakt","tung@gmail.com")
							.param("skills", "Java","Python")
							.param("vergangeneProjekte", Arrays.toString(new Long[]{1L})))
				.andDo(print())
				.andExpect(view().name("confirmationAdd"))
				.andExpect(model().attribute("person",hasItem(
					allOf(
						hasProperty("projekteId",is("1L"))
					)
		)))

//				.andExpect(request().attribute("vorname",is("Tom")))
//				.andExpect(request().attribute("jahreslohn",is("10000")))
//				.andExpect(request().attribute("projekteId",is( Arrays.toString(new Long[]{1L, 2L}))))
//				// .andExpect(view().name("confirmationAdd"))
		;
		verify(personRepository, times(1)).save(Mockito.isA(Person.class));
		verifyNoMoreInteractions(personRepository);
	}
	//TODO
	@Test
	public void editTEST_PersonnichtVorhanden() throws Exception {
		mockMvc.perform(get("/edit/{id}",2L))
				.andDo(print())
				.andExpect(status().isNotFound())
		;

		verify(personRepository, times(1)).findById(2L);
		verifyZeroInteractions(personRepository);
	}
	@Test
	public void editTEST_PersonVorhanden() throws Exception {
		when(personRepository.findById(	1L)).thenReturn(java.util.Optional.ofNullable(firstPerson));

		mockMvc.perform(get("/edit/{id}",1L))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("edit"))
				.andExpect(request().attribute("projekte",hasItem(
						allOf(
								hasProperty("titel",is("projekt4")),
								hasProperty("beschreibung",is("description")),
								hasProperty("startdatum",is("30.10.2018")),
								hasProperty("laufzeit",is("10 Monaten"))
						)
				)))
		;
		verify(personRepository, times(1)).findById(1L);
		verifyZeroInteractions(personRepository);
	}

}

