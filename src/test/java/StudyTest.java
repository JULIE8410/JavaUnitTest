import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    int value = 1;

    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

    @BeforeAll
    void beforeAll(){
        System.out.println(this);
        System.out.println("Before All");
        System.out.println(value++);
    }

    @AfterAll
    void alterAll(){
        System.out.println(this);
        System.out.println("After All");
        System.out.println(value);
    }

    @BeforeEach
    void beforeEach(){
        System.out.println(this);
        System.out.println("Before Each");
    }

    @AfterEach
    void afterEach(){
        System.out.println("After Each");
    }

    @Order(1)
    @DisplayName("반복")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo){
        System.out.println("Test " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    @Order(3)
    @DisplayName("Creating study")
    @ParameterizedTest(name = "{index} {displayName} - {0}")
    @CsvSource({"10, 'Java Study'", "20, 'Spring'"})
    void parameterizedTest1(@AggregateWith(StudyAggregator.class) Study study){
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} - {0}")
    @ValueSource(ints = {10, 20, 40})
    void parameterizedTest2(@ConvertWith(StudyConverter.class) Study study){
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter{
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));        }
    }

    @Order(2)
    @Test
    @Tag("fast")
    void create_condition_test() throws InterruptedException {
        Thread.sleep(1005L);
    }

    @Test
    @Tag("slow")
    void create(){
        System.out.println(this);
        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);
    }

    @Test
    @Tag("slow")
    void create2(){

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        String message = exception.getMessage();
        assertEquals("E: Limit should be greater than 0", exception.getMessage());
    }


    @Test
    @Tag("slow")
    @DisplayName("Test - Create Study")
    void create3(){
        Study study = new Study(10);

        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "Initial Status should be " + StudyStatus.DRAFT),
                () -> assertTrue(study.getLimit() > 0, "Limit should be greater than 0")
        );

        assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "Initial Status should be " + StudyStatus.DRAFT);
        assertTrue(study.getLimit() > 0, "Limit should be greater than 0");

    }





}