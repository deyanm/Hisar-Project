package com.example.mig;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import com.example.mig.network.ApiService;
import com.example.mig.repository.Repository;
import com.example.mig.utils.DataWrapper;
import com.example.mig.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class MainViewModelTest {

    @Mock
    ApiService apiEndPoint;
    @Mock
    private MainViewModel mainViewModel;
    @Mock
    Repository repository;
    @Mock
    Observer<DataWrapper> observer;
    @Mock
    LifecycleOwner lifecycleOwner;
    Lifecycle lifecycle;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        lifecycle = new LifecycleRegistry(lifecycleOwner);
        mainViewModel = new MainViewModel(repository);
        mainViewModel.getVersion().observe(lifecycleOwner, observer);
    }

    @Test
    public void testNull() {
//        when(apiClient.fetchNews()).thenReturn(null);
//        assertNotNull(mainViewModel.getNewsListState());
//        assertTrue(mainViewModel.getNewsListState().hasObservers());
    }

    @Test
    public void testApiFetchDataSuccess() {
//        when(mainViewModel.getVersion()).thenReturn(Single.just(new LocationResponse()));
//        mainViewModel.getVersions();
//        verify(observer).onChanged(NewsListViewState.LOADING_STATE);
//        verify(observer).onChanged(NewsListViewState.SUCCESS_STATE);
    }
}