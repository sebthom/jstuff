package net.sf.jstuff.core.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import net.sf.jstuff.core.localization.NumberHelper;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.math.NumberUtils;
import net.sf.jstuff.core.ref.FinalRef;
import net.sf.jstuff.core.ref.MutableRef;
import net.sf.jstuff.core.ref.Ref;
import net.sf.jstuff.core.reflection.StackTrace;

import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Reproduced with:
 * <ul>
 * <li> Oracle JDK5_u22 32bit using -server
 * <li> Oracle JDK6_u37 32bit using -server
 * <li> Oracle JDK8_u31 32bit using -server
 * <li> Oracle JDK8_u31 64bit using -XX:-UseCompressedOops (much higher probability compared to 32bit JDKs)
 * </ul>
 * http://www.infoq.com/articles/memory_barriers_jvm_concurrency
 * http://stackoverflow.com/questions/13578087/looking-for-a-test-to-reproduce-broken-double-checked-locking
 * http://cs.oswego.edu/pipermail/concurrency-interest/2015-January/013861.html
 * http://zeroturnaround.com/rebellabs/concurrency-torture-testing-your-code-within-the-java-memory-model/
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnsafePublicationTest extends TestCase
{
	private static abstract class AbstractConsumingActor implements Runnable
	{
		protected abstract Subject getSubject();

		public void run()
		{
			for (int i = 0; i < TOTAL_REPETITIONS; i++)
			{
				final Subject l = getSubject();
				if (l == null) return;
				// the field "subject" is declared as volatile, therefore the referenced object (if created and assigned by another thread) must be fully already initialized
				if (!l.isFullyInitialized()) FAILURES.incrementAndGet();

				Thread.yield();
			}
		}
	}

	private static abstract class AbstractPublishingActor implements Runnable
	{
		protected abstract void publishSubject();

		public void run()
		{
			for (int i = 0; i < TOTAL_REPETITIONS; i++)
			{
				publishSubject();

				Thread.yield();
			}
		}
	}

	private static final class Subject // must be static
	{
		private static final int NUMBER_OF_FIELDS = 40;

		// fields must be non-final to provoke instruction re-ordering
		int f01, f02, f03, f04, f05, f06, f07, f08, f09, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21, f22, f23, f24, f25,
				f26, f27, f28, f29, f30, f31, f32, f33, f34, f35, f36, f37, f38, f39, f40;

		public Subject(final int externalValue)
		{
			// the more field assignments we have, the higher the chance to encounter uninitialized values
			f40 = f39 = f38 = f37 = f36 = f35 = f34 = f33 = f32 = f31 = f30 = f29 = f28 = f27 = f26 = f25 = f24 = f23 = f22 = f21 = f20 = f19 = f18 = f17 = f16 = f15 = f14 = f13 = f12 = f11 = f10 = f09 = f08 = f07 = f06 = f05 = f04 = f03 = f02 = f01 = externalValue;
		}

		public boolean isFullyInitialized()
		{
			return 0 == NUMBER_OF_FIELDS
					- (f01 + f02 + f03 + f04 + f05 + f06 + f07 + f08 + f09 + f10 + f11 + f12 + f13 + f14 + f15 + f16 + f17 + f18 + f19
							+ f20 + f21 + f22 + f23 + f24 + f25 + f26 + f27 + f28 + f29 + f30 + f31 + f32 + f33 + f34 + f35 + f36 + f37
							+ f38 + f39 + f40);
		}
	}

	private static final Logger LOG = Logger.create();

	private static final long TOTAL_CPUs = Runtime.getRuntime().availableProcessors();
	private static final long TOTAL_REPETITIONS = 10 * NumberUtils.MILLION;
	private static final AtomicInteger FAILURES = new AtomicInteger();

	private static Subject subject = new Subject(1); // must be static to provoke instruction re-ordering
	private static volatile Subject subjectVolatile = new Subject(1);

	int subjectCtorArg = 1; // must be non-static, non-final to provoke instruction re-ordering

	private boolean _isJVMSupported()
	{
		if (!StringUtils.contains(System.getProperty("java.vm.name"), "HotSpot"))
		{
			LOG.info("This test is only supported on HotSpot JVM");
			return false;
		}

		final RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		final List<String> arguments = runtimeMxBean.getInputArguments();

		if ("64".equals(System.getProperty("sun.arch.data.model")))
		{
			if (!arguments.contains("-XX:-UseCompressedOops"))
			{
				LOG.info("This test requires JVM option -XX:-UseCompressedOops when run on 64bit JVM");
				return false;
			}
		}
		else if (!StringUtils.contains(System.getProperty("java.vm.name"), "Server"))
		{
			LOG.info("This test requires JVM option -server when run on a 32bit JVM");
			return false;
		}
		return true;
	}

	protected void _testPublication(final boolean isExpectedToFail, final Runnable publisher, final Runnable consumer)
			throws InterruptedException
	{
		if (isExpectedToFail && !_isJVMSupported()) return;

		FAILURES.set(0);

		final List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < TOTAL_CPUs / 2; i++)
		{
			threads.add(new Thread(publisher));
			threads.add(new Thread(consumer));
		}

		for (final Thread t : threads)
			t.start();

		for (final Thread t : threads)
			t.join();

		final String methodName = StackTrace.getCallerMethodName();
		LOG.info(methodName + "() -> " + new NumberHelper().getWholeNumberFormatted(FAILURES)
				+ " accesses to not fully initialized objects seen");

		if (isExpectedToFail)
			assertTrue(FAILURES.get() > 0);
		else
			assertEquals(0, FAILURES.get());
	}

	public void test1PublicationWithoutIndirection() throws Exception
	{
		_testPublication(//
				true, // expected to fail
				new AbstractPublishingActor()
					{
						@Override
						protected void publishSubject()
						{
							subject = new Subject(subjectCtorArg /*must be a non-static, non-final reference to provoke instruction re-ordering on 64bit OracleJVM*/);
						}
					}, //
				new AbstractConsumingActor()
					{
						@Override
						protected Subject getSubject()
						{
							return subject;
						}
					});
	}

	public void test2PublicationWithBrokenMemoryBarrier() throws Exception
	{
		_testPublication(//
				true, // expected to fail
				new AbstractPublishingActor()
					{
						@Override
						protected void publishSubject()
						{
							// non-final fields to not act as an implicit memory barrier
							final Ref<Subject> memoryBarrier = new MutableRef<Subject>(//
									new Subject(subjectCtorArg /*must be a non-static, non-final reference to provoke instruction re-ordering on 64bit OracleJVM*/) //
							);
							subject = memoryBarrier.get();
						}
					}, //
				new AbstractConsumingActor()
					{
						@Override
						protected Subject getSubject()
						{
							return subject;
						}
					});
	}

	public void test3PublicationWithFinalMemoryBarrier() throws Exception
	{
		_testPublication(//
				false, // not expected to fail
				new AbstractPublishingActor()
					{
						@Override
						protected void publishSubject()
						{
							// final fields act as an implicit memory barrier
							//  http://www.cs.umd.edu/~pugh/java/memoryModel/newFinal.pdf
							//  http://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.5.1
							//  http://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java -> FinalWrapper
							final Ref<Subject> memoryBarrier = new FinalRef<Subject>(//
									new Subject(subjectCtorArg /*must be a non-static, non-final reference to provoke instruction re-ordering on 64bit OracleJVM*/) //
							);
							subject = memoryBarrier.get();
						}
					}, //
				new AbstractConsumingActor()
					{
						@Override
						protected Subject getSubject()
						{
							return subject;
						}
					});
	}

	public void test4PublicationWithVolatileMemoryBarrier() throws Exception
	{
		_testPublication(//
				false, // not expected to fail
				new AbstractPublishingActor()
					{
						@Override
						protected void publishSubject()
						{
							subjectVolatile = new Subject(subjectCtorArg /*must be a non-static, non-final reference to provoke instruction re-ordering on 64bit OracleJVM*/);
						}
					}, //
				new AbstractConsumingActor()
					{
						@Override
						protected Subject getSubject()
						{
							return subjectVolatile;
						}
					});
	}
}
