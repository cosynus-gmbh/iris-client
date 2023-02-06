package iris.client_bff.kir_tracing;

import java.io.Serial;

public class KirTracingException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 8656028062005756669L;

	public KirTracingException(String msg, Throwable e) {
		super(msg, e);
	}
}
