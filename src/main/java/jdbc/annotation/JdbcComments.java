/*
 * Programming by: George <GeorgeNiceWorld@gmail.com>
 * Copyright (C) George And George Companies to Work For, All Rights Reserved.
 */
package jdbc.annotation;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface JdbcComments {

    public JdbcCommentItem[] value();
}
