import me.coley.recaf.Recaf;
import me.coley.recaf.plugin.PluginsManager;
import me.coley.recaf.plugin.api.BasePlugin;

public class RecafStart {
    public static void main(String[] args) {
        try {
            String mainClass = null;
            for (int i = 0; i < args.length; i++) {
                String a = args[i];
                if (a.equals("--mainClass")) {
                    mainClass = args[i + 1];
                    break;
                }
            }
            if (mainClass == null) {
                throw new IllegalStateException("You must specify a plugin main class");
            }
            Class<?> clazz = Class.forName(mainClass);
            System.out.println("Attempting to load " + mainClass + "!");
            try {
                Class<? extends BasePlugin> plugin = clazz.asSubclass(BasePlugin.class);
                PluginsManager.getInstance().addPlugin((BasePlugin) plugin.newInstance());
                System.out.println("Added plugin! Launching Recaf...");
                Recaf.main(new String[0]);
            } catch (Throwable t) {
                if (t instanceof ClassCastException) {
                    throw new IllegalStateException("FAILED! Please verify your plugin implements BasePlugin");
                } else {
                    t.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}