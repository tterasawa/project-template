package so.sabami.template.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * @author usr160056
 * @since 2015/03/06
 */
public class EncryptException
        extends NestedRuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -4371892184762644203L;

    /**
     * コンストラクタ
     * @param cause 例外
     */
    public EncryptException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
