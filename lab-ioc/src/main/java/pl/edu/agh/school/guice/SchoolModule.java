package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


public class SchoolModule extends AbstractModule {

    @Override
    protected void configure(){
        bind(String.class)
                .annotatedWith(Names.named("teachersStorageFileName"))
                .toInstance("teachers.dat");

        bind(String.class)
                .annotatedWith(Names.named("classStorageFileName"))
                .toInstance("classes.dat");
    }

}
