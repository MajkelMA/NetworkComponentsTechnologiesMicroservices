package Repository;

import java.util.List;

public interface Filler<T> {
    void autoFill(List<T> destination);
}
