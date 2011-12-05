package com.aug3.sys.xml;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Element;

import com.aug3.sys.CommonException;

/**
 * Use this class to create or populate beans from XML elements. This class will
 * populate existing beans with values from the element attributes if they
 * exist, performing the necessary conversions. Also, it can create elements
 * straight from the XML if the element has a <code>class</code> attribute
 * defined.
 * 
 * @author xial
 */
public class XmlBeanBuilder {

	private static final String CLASS_ATTRIBUTE = "class";

	private Class defaultBeanClazz = Object.class;

	/**
	 * Populates the bean with the corresponding values in the XML element's
	 * attributes and sub-elements. Attributes and sub-elements for which the
	 * bean does not have a corresponding field are ignored.
	 * 
	 * Limitations:
	 * <ol>
	 * <li>population is not recursive</li>
	 * <li>only the text fields of the sub-elements are used</li>
	 * </ol>
	 * 
	 * @param bean
	 *            the bean to be populated.
	 * @param element
	 *            the xml element whose attributes will be used to populate the
	 *            bean.
	 * 
	 * @throws CommonException
	 *             if it cannot populate the bean
	 */
	public void populate(Object bean, Element element) throws CommonException {
		try {
			XmlElement el = new XmlElement(element);
			BeanUtils.populate(bean, el.getAttributes());
			processSubElements(bean, el.getChildElements());
		} catch (IllegalAccessException e) {
			throw new CommonException(
					"could not access set statement for bean", e);
		} catch (InvocationTargetException e) {
			throw new CommonException("could not run set statement for bean", e);
		}

	}

	/**
	 * Creates a bean from them XML definition (using the class attribute), but
	 * does not populate it with any of the other attribute values.
	 */
	public Object createEmptyBean(Element element) throws CommonException {
		try {
			validateNotNull(element, "element cannot be null");
			Class clazz = defaultBeanClazz;

			if (element.hasAttribute(CLASS_ATTRIBUTE)) {
				String className = element.getAttribute(CLASS_ATTRIBUTE);
				clazz = Class.forName(className);
			}

			Object bean = clazz.newInstance();
			return bean;
		} catch (ClassNotFoundException e) {
			throw new CommonException("Did not find bean class.", e);
		} catch (IllegalAccessException e) {
			throw new CommonException("Could not access constructor.", e);
		} catch (InstantiationException e) {
			throw new CommonException("Could not create bean instance.", e);
		}

	}

	/**
	 * Creates a bean and populates it with the attribute values of the DOM
	 * element. The bean class is defined by the element's <code>class</code>
	 * parameter if it exists, or the default class if not.
	 * 
	 * @param element
	 *            The element whose attributes are used to populate the bean
	 * 
	 * @return the new, populated bean.
	 * 
	 * @throws IllegalAccessException
	 *             if the set method cannot be accessed.
	 * 
	 * @throws InvocationTargetException
	 *             if the execution of the bean's set method throws an
	 *             exception.
	 * 
	 * @throws ClassNotFoundException
	 *             if it cannot find the bean class.
	 * 
	 * @throws InstantiationException
	 *             if it cannot create an instance of the bean class.
	 * @throws IllegalArgumentException
	 *             if <code>element</code> is null.
	 */
	public Object createBean(Element element) throws CommonException {

		Object bean = createEmptyBean(element);
		populate(bean, element);
		return bean;
	}

	/**
	 * Sets the default class to use for bean creation if one is not provided as
	 * a <code>class</code> attribute in the element.
	 * 
	 * @param clazz
	 *            the default class to use for bean construction.
	 * 
	 * @throws IllegalArgumentException
	 *             if the clazz is null.
	 */
	public void setDefaultBeanClass(Class clazz) {
		validateNotNull(clazz, "bean class must not be null");
		defaultBeanClazz = clazz;
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ----------------------------------------------------------------------

	/**
	 * Adds the subelements to the bean, assuming that all children are element
	 * nodes.
	 */
	private void processSubElements(Object bean, List children)
			throws InvocationTargetException, IllegalAccessException {

		for (Iterator it = children.iterator(); it.hasNext();) {
			XmlElement child = (XmlElement) it.next();
			BeanUtils.setProperty(bean, child.getTagName(), child.getText());
		}

	}

	private void validateNotNull(Object obj, String msg) {
		if (obj == null) {
			throw new IllegalArgumentException(msg);
		}
	}
}
