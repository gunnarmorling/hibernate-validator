module org.hibernate.validator.engine {
    exports org.hibernate.validator;
    exports org.hibernate.validator.cfg;
    exports org.hibernate.validator.cfg.context;
    exports org.hibernate.validator.cfg.defs;
    exports org.hibernate.validator.constraints;
    exports org.hibernate.validator.constraints.br;
    exports org.hibernate.validator.constraintvalidation;
    exports org.hibernate.validator.constraintvalidators;
    exports org.hibernate.validator.engine;
    exports org.hibernate.validator.group;
    exports org.hibernate.validator.messageinterpolation;
    exports org.hibernate.validator.parameternameprovider;
    exports org.hibernate.validator.path;
    exports org.hibernate.validator.resourceloading;
    exports org.hibernate.validator.spi.cfg;
    exports org.hibernate.validator.spi.group;
    exports org.hibernate.validator.spi.resourceloading;
    exports org.hibernate.validator.spi.time;
    exports org.hibernate.validator.spi.valuehandling;
    exports org.hibernate.validator.valuehandling;

    exports org.hibernate.validator.internal.util.logging to jbosslogging;
    exports org.hibernate.validator.internal.xml to java.xml.bind;

    requires javax.validation;
    requires jodatime;
    requires javaxelapi;
    requires jsoup;
    requires jbossloggingannotations;
    requires jbosslogging;
    requires classmate;
    requires paranamer;
    requires hibernatejpa;
    requires java.xml.bind;
    requires java.xml;
    requires java.scripting;
    requires javafx.base;

    provides javax.validation.spi.ValidationProvider with org.hibernate.validator.HibernateValidator;

    uses javax.validation.ConstraintValidator;
}
