package org.example;

import org.example.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Objects;

@SpringBootTest
class EmployeeTest {

    static ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    static ReferenceQueue<Object> softReferenceQueue = new ReferenceQueue<>();

    @BeforeEach
    public void beforeAll() {
        System.out.println("------------------------------------------------------------------");
    }

    @AfterEach
    public void afterAll() {
        System.out.println("------------------------------------------------------------------");
    }

    @Test
    void testObjectCleanup() {
        System.out.println("Test Object Cleanup :");
        Employee employee = new Employee("Saurabh", 1);
        SoftReference<Employee> softReference = getSoftReference(employee);
        System.out.println("Details before nullifying reference :\n" + softReference.get());
        employee = null;
        System.out.println("Details after nullifying reference :\n" + softReference.get());
        System.gc();//will collect soft references only if JVM is about to face OOM
        System.out.println("Details after nullifying reference and after performing garbage collection:\n" + softReference.get());
        System.out.println("Triggering cleanup :");
        Objects.requireNonNull(softReference.get()).close();
        System.out.println(softReference.get());
        System.out.println("Reference queue size :"+referenceQueue.poll());
        softReference.clear();
        System.out.println(softReference.get());
    }

    @Test
    void testObjectCleanupWithPhantomReference() throws Exception {
        System.out.println("Test Object Cleanup with Phantom Finalizer :");
        Employee employee = new Employee("Saurabh", 1);
        WeakReference<Employee> weakReference = getWeakReference(employee);
        SoftReference<Employee> softReference = getSoftReference(employee);
        PhantomFinalizer<Employee> phantomFinalizer = getPhantomReferenceWithFinalizer(employee);
        System.out.println("Details before nullifying reference :\n" + softReference.get());
        employee = null;
        System.out.println("Details after nullifying original reference :");
        System.out.println("is weak reference enqueued : " + weakReference.isEnqueued());
        System.out.println("is soft reference enqueued : " + softReference.isEnqueued());
        System.out.println("is phantom reference enqueued : " + phantomFinalizer.isEnqueued());
        System.gc();//will collect soft references only if JVM is about to face OOM
        System.out.println("Details after nullifying reference and after performing garbage collection:");
        System.out.println("is weak reference enqueued : " + weakReference.isEnqueued());
        System.out.println("is soft reference enqueued : " + softReference.isEnqueued());
        System.out.println("is phantom reference enqueued : " + phantomFinalizer.isEnqueued());
        softReference = null;
        System.gc();//will collect soft references only if JVM is about to face OOM
        System.out.println("Details after nullifying reference and after performing garbage collection:");
        System.out.println("is weak reference enqueued : " + weakReference.isEnqueued());
        System.out.println("is soft reference enqueued : " + softReferenceQueue.poll());
        System.out.println("is phantom reference enqueued : " + phantomFinalizer.isEnqueued());
        phantomFinalizer.triggerCleanup();

    }

    @Test
    void testWeakReferences() {
        System.out.println("Weak Reference Testing :");
        Employee employee = new Employee("Saurabh", 1);
        WeakReference<Employee> weakReference = getWeakReference(employee);
        System.out.println("Details before nullifying reference :\n" + weakReference.get());
        employee = null;
        System.out.println("Details after nullifying reference :\n" + weakReference.get());
        System.gc();//will collect weak references
        System.out.println("Details after nullifying reference and after performing garbage collection:\n" + weakReference.get());
    }

    @Test
    void testSoftReferences() {
        System.out.println("Soft Reference Testing :");
        Employee employee = new Employee("Saurabh", 1);
        SoftReference<Employee> softReference = getSoftReference(employee);
        System.out.println("Details before nullifying reference :\n" + softReference.get());
        employee = null;
        System.out.println("Details after nullifying reference :\n" + softReference.get());
        System.gc();//will collect soft references only if JVM is about to face OOM
        System.out.println("is soft reference enqueued : " + softReference.isEnqueued());
        System.out.println("Details after nullifying reference and after performing garbage collection:\n" + softReference.get());
    }

    @Test
    void testPhantomReferences() throws InterruptedException {
        System.out.println("Phantom Reference Testing :");
        Employee employee = new Employee("Saurabh", 1);
        PhantomReference<Employee> phantomReference = getPhantomReference(employee);
        System.out.println("Details before nullifying reference :\n" + phantomReference.get());
        employee = null;
        System.out.println("Details before nullifying reference :\n" + phantomReference.get());
        System.gc();//Adds phantomReference into reference queue. We never get referent back from phantom reference object
        System.out.println("Details before nullifying reference and after performing garbage collection:\n" + phantomReference.get());
        referenceQueue.remove();
    }

    private static WeakReference<Employee> getWeakReference(Employee object) {
        return new WeakReference<>(object,referenceQueue);
    }

    private static SoftReference<Employee> getSoftReference(Employee object) {
        return new SoftReference<>(object, softReferenceQueue);
    }

    private static PhantomReference<Employee> getPhantomReference(Employee object) {
        return new PhantomReference<>(object, referenceQueue);
    }

    private static PhantomFinalizer<Employee> getPhantomReferenceWithFinalizer(Employee object) {
        return new PhantomFinalizer<>(object, referenceQueue);
    }

    static class PhantomFinalizer<T extends AutoCloseable> extends PhantomReference<T>{

        /**
         * Creates a new phantom reference that refers to the given object and
         * is registered with the given queue.
         *
         * <p> It is possible to create a phantom reference with a <tt>null</tt>
         * queue, but such a reference is completely useless: Its <tt>get</tt>
         * method will always return null and, since it does not have a queue, it
         * will never be enqueued.
         *
         * @param referent the object the new phantom reference will refer to
         * @param q        the queue with which the reference is to be registered,
         */
        public PhantomFinalizer(T referent, ReferenceQueue<? super Object> q) {
            super(referent, q);
        }

        public void triggerCleanup() throws Exception {
           /* ((T) Objects.requireNonNull(this.weakReference.get())).close();
            System.out.println(this.weakReference.get());*/
        }
    };
}
