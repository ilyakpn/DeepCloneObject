import java.util.HashMap;
import java.util.Map;

public class ByRefMap {
    public Map<Object, Object> refMap;

    public ByRefMap() {
        this.refMap = new HashMap<>();
    }

    public boolean containsKeyRef(Object obj) {
        if (refMap.isEmpty())
            return false;

        for (Map.Entry<Object, Object> entry : refMap.entrySet()) {
            if (entry.getKey() == obj)
                return true;
        }
        return false;
    }

    public Object getValueByKeyRef(Object obj) {
        for (Map.Entry<Object, Object> entry : refMap.entrySet()) {
            if (entry.getKey() == obj)
                return entry.getValue();
        }
        return null;
    }
}
