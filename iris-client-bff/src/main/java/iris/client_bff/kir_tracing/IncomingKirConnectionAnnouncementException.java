package iris.client_bff.kir_tracing;

/**
 * @author Jens Kutzsche
 */
public class IncomingKirConnectionAnnouncementException extends RuntimeException {

	private static final long serialVersionUID = -62922796972802753L;

	public IncomingKirConnectionAnnouncementException(String msg, Throwable e) {
		super(msg, e);
	}
}
