package iris.client_bff.kir_tracing;

/**
 * @author Jens Kutzsche
 */
public class KirTracingException extends RuntimeException {

	private static final long serialVersionUID = -62922796972802753L;

	public KirTracingException(String msg, Throwable e) {
		super(msg, e);
	}
}
