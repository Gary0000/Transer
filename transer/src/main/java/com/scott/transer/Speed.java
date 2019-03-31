package com.scott.transer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <P>Author: shijiale-PUBG</P>
 * <P>Date: 2019/3/31</P>
 * <P>Email: shilec@126.com</p>
 */

@Retention(CLASS)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER})
public @interface Speed {

}
