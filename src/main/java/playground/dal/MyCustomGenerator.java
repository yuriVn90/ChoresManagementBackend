package playground.dal;

import java.io.Serializable;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class MyCustomGenerator implements IdentifierGenerator {

    public static final String generatorName = "randomGen";

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object object) throws HibernateException {
        Long longNum = new Random().nextLong(); 
        return (longNum < 0)? longNum*(-1) : longNum; 
    }
}