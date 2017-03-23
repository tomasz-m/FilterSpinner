package info.tomaszminiach.libshow;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import info.tomaszminiach.superspinner.SuperSpinner;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Mock
    Context context;
    @Mock
    ArrayAdapter<String> simpleAdapter;
    @Mock
    View.OnClickListener onClickListener;

    SuperSpinner superSpinner;
    List<String> array;


    @Before
    public void configureTest(){
        superSpinner = new SuperSpinner(context);
        array = new ArrayList<>(3);
        array.add("aaa");
        array.add("bbb");
        array.add("ccc");

        simpleAdapter = Mockito.mock(ArrayAdapter.class);
        onClickListener = Mockito.mock(View.OnClickListener.class);

        Mockito.when(simpleAdapter.getCount()).thenReturn(3);
        Mockito.when(simpleAdapter.getItem(0)).thenReturn(array.get(0));
        Mockito.when(simpleAdapter.getItem(1)).thenReturn(array.get(1));
        Mockito.when(simpleAdapter.getItem(2)).thenReturn(array.get(2));

        superSpinner.setAdapter(simpleAdapter);

    }

    @Test
    public void dummyTest() throws Exception {
        superSpinner.setFilterable(true);
        assertEquals(superSpinner.isFilterable(), true);
    }
    @Test
    public void dummyAdapterTest() throws Exception {
        String item  = simpleAdapter.getItem(2);
        assertEquals(item, array.get(2));
    }

    @Test
    public void nothingSelectedTest() throws Exception {
        assertEquals(superSpinner.getSelectedItem(), null);
    }

    @Test
    public void simpleSelectTest() throws Exception {
        superSpinner.selectItem(2);
        assertEquals((String)superSpinner.getSelectedItem(), array.get(2));
    }


}