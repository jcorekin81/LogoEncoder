package login;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class SpringJSFUtil {

    public static <T> T getBean(String beanName) {
        if (beanName == null) {
            return null;
        }
        return getValue("#{" + beanName + "}");
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(String expression) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        Object value = app.evaluateExpressionGet(context,
                expression, Object.class);
        return (T) value;
    }
}