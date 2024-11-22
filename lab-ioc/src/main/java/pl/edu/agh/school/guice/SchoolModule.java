package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import pl.edu.agh.logger.FileMessageSerializer;
import pl.edu.agh.logger.Logger;


public class SchoolModule extends AbstractModule {

    @Override
    protected void configure(){
        bind(String.class)
                .annotatedWith(Names.named("teachersStorageFileName"))
                .toInstance("teachers.dat");

        bind(String.class)
                .annotatedWith(Names.named("classStorageFileName"))
                .toInstance("classes.dat");

        bind(Logger.class).toInstance(createLogger());
    }

    private Logger createLogger() {
        Logger logger = new Logger();
        logger.registerSerializer(new FileMessageSerializer("persistence.log"));
        return logger;
    }

}
