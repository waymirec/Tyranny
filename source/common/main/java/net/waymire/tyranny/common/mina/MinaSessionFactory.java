package net.waymire.tyranny.common.mina;

import java.lang.reflect.Constructor;

import org.apache.mina.core.session.IoSession;

import net.waymire.tyranny.common.net.IpSession;

public class MinaSessionFactory
{
	public static <S extends IpSession> S create(Class<S> sessionType, IoSession ioSession)
	{
		try
		{
			Constructor<? extends S> ctor = sessionType.getConstructor(IoSession.class);
			S session = ctor.newInstance(ioSession);
			return session;
		}
		catch(ReflectiveOperationException reflectiveOperationException)
		{
			throw new RuntimeException(reflectiveOperationException);
		}
	}
}
