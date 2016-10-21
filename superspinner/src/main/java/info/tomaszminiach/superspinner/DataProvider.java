package info.tomaszminiach.superspinner;

import java.util.List;

/**
 * Created by Tomek on 2016-10-21.
 */
public interface DataProvider {

    List<String> getItems(String filter);

}
