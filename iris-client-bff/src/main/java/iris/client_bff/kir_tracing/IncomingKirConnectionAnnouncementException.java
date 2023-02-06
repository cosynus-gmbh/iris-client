package iris.client_bff.kir_tracing;

import java.io.Serial;

/**
 * @author Jens Kutzsche
 */
public class IncomingKirConnectionAnnouncementException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -2438880920105814164L;

	public IncomingKirConnectionAnnouncementException(String msg, Throwable e) {
		super(msg, e);
	}
}
