package pl.edu.agh.school;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.guice.SchoolModule;

import static org.junit.jupiter.api.Assertions.assertSame;

public class SchoolModuleTest {
    @Test
    public void testLoggerIsSingleton(){
        Injector injector = Guice.createInjector(new SchoolModule());

        Logger logger1 = injector.getInstance(Logger.class);
        Logger logger2 = injector.getInstance(Logger.class);

        assertSame(logger1, logger2);

    }
}
