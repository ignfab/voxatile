package fr.ign.voxatile.core.parameters;

import java.util.List;
import java.util.Objects;

import tools.jackson.core.JacksonException;
import tools.jackson.core.TokenStreamLocation;

/**
 * Exception thrown if a problem occurs during parsing.
 */
public class ParseException extends Exception {

    /**
     * Creates a ParseException.
     *
     * @param message Message explaining the problem
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a ParseException.
     *
     * @param message Message explaining the problem
     * @param cause Throwable that caused the problem
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a ParseException.
     *
     * @param cause Throwable that caused the problem
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new {@code ParseException} from a {@link JacksonException}.
     * @param je Original exception from Jackson
     * @return Parse exception with appropriate info
     */
    public static ParseException fromJacksonException(JacksonException je) {
        StringBuilder sb = new StringBuilder(je.getClass().getSimpleName() + ": " + je.getOriginalMessage());
        TokenStreamLocation location = je.getLocation();
        if (location != TokenStreamLocation.NA)
            sb.append("\nLocation: line ").append(location.getLineNr()).append(", column ").append(location.getColumnNr());
        List<JacksonException.Reference> path = je.getPath();
        if (!path.isEmpty()) {
            sb.append("\nPath: ");
            for (int i = 0; i < path.size(); i++) {
                JacksonException.Reference ref = path.get(i);
                if (ref.getIndex() >= 0)
                    sb.append('[').append(ref.getIndex()).append(']');
                else {
                    if (i > 0)
                        sb.append('.');
                    sb.append(Objects.requireNonNullElse(ref.getPropertyName(), "?"));
                }
            }
        }
        ParseException pe = new ParseException(sb.toString(), je.getCause());
        pe.setStackTrace(je.getStackTrace());
        return pe;
    }
}
