package com.soul.wk.netty.privateprotocol.edecoder;

import org.jboss.marshalling.*;

import java.io.IOException;

public final class MarshallingCodecFactory {

    protected static Marshaller buildMarshalling() throws IOException {

        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        return marshaller;
    }

    protected static Unmarshaller buildUnMarshaling() throws IOException {

        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);

        return unmarshaller;
    }


}
