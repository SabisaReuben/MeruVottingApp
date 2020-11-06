package com.example.mustapp;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mustapp.databinding.FragmentCandidateRegistrationBinding;
import com.example.mustapp.databinding.FragmentLoginBinding;
import com.example.mustapp.databinding.FragmentRegistrationBinding;
import com.example.network.NetworkService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ValidationViewModel extends AndroidViewModel {
    private static final String TAG = ValidationViewModel.class.getName();
    private static final List<Candidate> selectedCandidates = new ArrayList<>();
    private MutableLiveData<List<Candidate>> selectedCandidatesMutableLiveData = new MutableLiveData<>();
    /**
     *
     */
    private MutableLiveData<Voter> voterMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Voter> voteRegistrationMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Candidate> candidateMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginFragment.Login> loginLayoutValidator = new MutableLiveData<>();
    private MutableLiveData<Candidate> candidateValidatorLiveData = new MutableLiveData<>();
    private MutableLiveData<List<VoteFragment.ToggleManager>> toggleLiveData = new MutableLiveData<>();
    private static List<VoteFragment.ToggleManager> toggleManagerList = new ArrayList<>();
    static {
        for (int i=0;i<9;i++){
            VoteFragment.ToggleManager toggleManager = new VoteFragment.ToggleManager();
            toggleManager.setVisible(true);
            toggleManager.setId(i);
            toggleManagerList.add(toggleManager);
        }
    }

    /**
     * Observe the liveData to control the layout visibility of items. The list contains 9 items
     * representing the 9 schools. The values change depending on user action to either expand
     * (show more) or expand  less.
     * @return MutableLiveData
     */
    public MutableLiveData<List<VoteFragment.ToggleManager>> getToggleLiveData() {
        toggleLiveData.postValue(toggleManagerList);
        return toggleLiveData;
    }

    private static final LoginFragment.Login login = new LoginFragment.Login();
    /**
     * Used for Validation and input capture in {@link FragmentCandidateRegistrationBinding}
     */
    private static final Candidate candidate = new Candidate();
    /**
     * Layout validation and input capture instance. Set layout
     **/
    private static final Voter voter = new Voter();
    /**
     * functional interface that reports a tagged error.
     */
    private ErrorListener listener;
    /**
     * Call back for after completion of task
     */
    private Callback callback;
    /**
     * Layout binding instance of a CandidateRegistration fragment
     */
    private FragmentCandidateRegistrationBinding candidateRegistrationBinding;
    /**
     * layout binding instance of login fragment
     **/
    private FragmentLoginBinding loginBinding;
    /**
     * Layout binding instance of VoterRegistration fragment
     */
    private FragmentRegistrationBinding voterRegistrationBinding;
    private  RegistrationApi registrationApi;
    public ValidationViewModel(@NonNull Application application) {
        super(application);
        registrationApi= NetworkService.createService(RegistrationApi.class,
                application);
    }

    public  List<Candidate> getSelectedCandidates() {
        return selectedCandidates;
    }

    public MutableLiveData<List<Candidate>> getSelectedCandidatesMutableLiveData() {
        selectedCandidatesMutableLiveData.postValue(selectedCandidates);
        return selectedCandidatesMutableLiveData;
    }

    boolean addCandidate(Candidate candidate) {
        boolean candidateExists = false;
        for (Candidate can: selectedCandidates) {
            if (can != null) {
                if (candidate.getPositionVying().equals(can.getPositionVying())) {
                    candidateExists = true;
                    break;
                }
            }
        }
        if (!candidateExists) {
            selectedCandidates.add(candidate);
            selectedCandidatesMutableLiveData.postValue(selectedCandidates);
            return true;
        }
        return false;
    }

    /**
     * Observe the LiveData for input value changes and for validation purposes.
     * Observers should set binding layout.
     *
     * @return MutableLiveData
     */
    MutableLiveData<Candidate> getCandidateValidatorLiveData() {
        //set a layout for validation
        candidate.setLayout(candidateRegistrationBinding);
        candidateValidatorLiveData.postValue(candidate);
        return candidateValidatorLiveData;
    }

    /**
     * Method sets a layout binding object used to pass validation errors. This is required in
     * {@link Candidate} for validation
     *
     * @param binding {@link FragmentCandidateRegistrationBinding}
     * @see this#getCandidateValidatorLiveData()
     */
    public void setCandidateLayoutBinding(@Nullable FragmentCandidateRegistrationBinding binding) {
        this.candidateRegistrationBinding = binding;
    }


    /**
     * The live data holds the instance of a logged in candidate.
     * The value is added only when a candidate logs in;
     *
     * @return MutableLiveData
     * @see this#loginUser(LoginFragment.Login, Context)   Entity requiring authentication calls this method
     */
    public MutableLiveData<Candidate> getLoggedCandidateMutableLiveData() {
        return candidateMutableLiveData;
    }

    /**
     * The method hold instance used for layout validation and input capture
     *
     * @return MutableLiveData
     */
    MutableLiveData<LoginFragment.Login> getLoginLayoutValidator() {
        login.setLayout(loginBinding);
        loginLayoutValidator.postValue(login);
        return loginLayoutValidator;
    }

    /**
     * Methods sets the layout for validation of login fragment layout
     * Call the method before observing <code>getLoginLayoutValidator</code>
     * For example:
     * <pre>
     *     FragmentLoginBinding binding = ....
     *     ValidationViewModel viewModel = ....
     *     viewModel.setLoginLayoutBinding(binding);
     *     viewModel.getLoginLayoutValidator().observe(requireActivity(),binding::setLogin)
     *
     * </pre>
     *
     * @param binding Layout binding instance
     * @see this#getLoginLayoutValidator()
     */
    public void setLoginLayoutBinding(FragmentLoginBinding binding) {
        this.loginBinding = binding;
    }


    /**
     * Method enables activity or fragment to register to receive any error update.
     * Observers can filter this updates using the tag.
     * for example
     * <pre>
     *          viewModel.setErrorListener((throwable,tag)->{
     *              if(TAG.equals(tag)){
     *                  //handle the update
     *              }
     *          })
     *      </pre>
     *
     * @param listener {@link ErrorListener}
     */
    void setErrorListener(ErrorListener listener) {
        this.listener = listener;
    }

    /**
     * Method registers fragment to receive update after successful task completion or failure such
     * as in a login fragment or registration fragment
     *
     * @param listener {@link Callback}
     */
    void setCallback(Callback listener) {
        this.callback = listener;
    }

    /**
     * @return MutableLiveData
     */
    MutableLiveData<Voter> getLoggedVoterLiveData() {
        return voterMutableLiveData;
    }


    /**
     * Observe the LiveData for layout validation during voter registration.
     *
     * @return ViewModel
     */
    MutableLiveData<Voter> getVoterRegistrationMutableLiveData() {
        voter.setLayoutBinding(voterRegistrationBinding);
        voteRegistrationMutableLiveData.postValue(voter);
        return voteRegistrationMutableLiveData;
    }

    /**
     * Method sets layout
     *
     * @param voterRegistrationBinding {@link FragmentRegistrationBinding}
     */
    void setVoterRegistrationBinding(FragmentRegistrationBinding voterRegistrationBinding) {
        this.voterRegistrationBinding = voterRegistrationBinding;
    }

    /**
     * The method sends the user data to backend for storage
     *
     * @param voter {@link Voter} rep. entity requesting registration
     */
    void registerVoter(Voter voter,String encodedString) {
        if (encodedString == null || TextUtils.isEmpty(encodedString)) {
            if (callback != null) {
                callback.failure("Please capture photo to continue");
            }
            return;
        }
        Map<String, String> map = getVoterMap(voter);
        map.put("image", encodedString);
        Call<com.example.model.Response> responseCall = registrationApi.register(map,
                "voter.php");
        responseCall.enqueue(new retrofit2.Callback<com.example.model.Response>() {
            @Override
            public void onResponse(@NonNull Call<com.example.model.Response> call,
                                   @NonNull Response<com.example.model.Response> response) {
                if (response.body() != null) {
                    com.example.model.Response response1 = response.body();
                    if (response1.isSuccess()) {
                        if (callback != null) {
                            callback.success(response1.getMessage());
                        }
                    }else if(callback!=null) callback.failure(response1.getMessage());

                }

            }

            @Override
            public void onFailure(@NonNull Call<com.example.model.Response> call, @NonNull Throwable t) {
                if(listener!=null) listener.error(t, "voterRegistrationFragment");
            }
        });
    }

    void registerCandidate(Candidate candidate) {
        Log.d(TAG, candidate.toString());
        Call<com.example.model.Response> responseCall = registrationApi.register(getCandindateMap(
                candidate), "candidate.php");
        responseCall.enqueue(new retrofit2.Callback<com.example.model.Response>() {
            @Override
            public void onResponse(@NonNull Call<com.example.model.Response> call,
                                   @NonNull Response<com.example.model.Response> response) {
                if (response.body() != null) {
                    com.example.model.Response response1 = response.body();
                    if (response1.isSuccess()) {
                        if (callback != null) {
                            callback.success(response1.getMessage());
                        }
                    }else if(callback!=null) callback.failure(response1.getMessage());

                }

            }

            @Override
            public void onFailure(@NonNull Call<com.example.model.Response> call, @NonNull Throwable t) {
                if(listener!=null) listener.error(t, "candidateRegistrationFragment");
            }
        });
    }
    private Map<String,String> getVoterMap(Voter voter){
        Map<String, String> map = new HashMap<>();
        map.put("voter_name", (voter.getFirstName() + " " + voter.getSecondName()));
        map.put("nationality", voter.getNationality());
        map.put("school_id", voter.getSchoolId());
        map.put("password", voter.getPassword());
        map.put("email", voter.getEmail());
        map.put("phone_number", voter.getPhoneNumber());
        map.put("school", voter.getSchool());
        map.put("year_of_study", voter.getYearOfStudy());
        return map;
    }
    private Map<String,String> getCandindateMap(Candidate candidate){
        Map<String, String> map = new HashMap<>();
        map.put("school_id", candidate.getSchoolId());
        map.put("position",candidate.getPositionVying());
        map.put("party", candidate.getParty());
        return map;
    }

    /**
     * Use the method to login a user.
     *
     * @param login The login object representing user entity requesting authentication
     */
    void loginUser(LoginFragment.Login login, Context context) {
        Log.d(TAG,login.toString());
        Authenticator authenticator = NetworkService.createService(Authenticator.class,
                context);
        switch (login.getRole()) {
            case "Voter":
                Call<Voter> voterCall = authenticator.loginUser(login.getEmailLogin(),
                        login.getLoginPassword(),"user_login.php");
                voterCall.enqueue(new retrofit2.Callback<Voter>() {
                    @Override
                    public void onResponse(@NonNull Call<Voter> call, @NonNull Response<Voter> response) {
                        if (( response.body() )!= null && response.isSuccessful()) {
                            Voter voter = response.body();
                            if(callback!=null) {
                                if(voter.isSuccess()){
                                    callback.success(voter.getMessage());
                                    voterMutableLiveData.postValue(voter);
                                }
                                else callback.failure(voter.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Voter> call, @NonNull Throwable t) {
                        if(listener!=null) listener.error(t, "loginFragment");
                    }
                });
                break;
            case "Candidate":
                Call<Candidate> candidateCall = authenticator.loginCandidate(login.getLoginPassword()
                        , login.getEmailLogin(), "candidate_login.php","application/json");
                candidateCall.enqueue(new retrofit2.Callback<Candidate>() {
                    @Override
                    public void onResponse(@NonNull Call<Candidate> call,@NonNull Response<Candidate> response) {
                        if (response.body() != null) {
                            Candidate candidate = response.body();
                            if(candidate.isSuccess()) candidateMutableLiveData.postValue(candidate);
                            if(callback!= null){
                                if (candidate.isSuccess()) {
                                    callback.success(candidate.getMessage());
                                }else callback.failure(candidate.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Candidate> call, @NonNull Throwable t) {
                        if(listener!=null) listener.error(t, "loginFragment");
                    }
                });
                break;
            case "Admin":
                Call<Voter> adminCall = authenticator.loginUser(login.getEmailLogin(),
                        login.getLoginPassword(),"admin_login.php");
                adminCall.enqueue(new retrofit2.Callback<Voter>() {
                    @Override
                    public void onResponse(@NonNull Call<Voter> call, @NonNull Response<Voter> response) {
                        if (( response.body() )!= null && response.isSuccessful()) {
                            Voter voter = response.body();
                            if(callback!=null) {
                                if(voter.isSuccess()){
                                    callback.success(voter.getMessage());
                                     voterMutableLiveData.postValue(voter);
                                }
                                else callback.failure(voter.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Voter> call, @NonNull Throwable t) {
                        if(listener!=null) listener.error(t, "loginFragment");
                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + login.getRole());
        }
    }



    /**
     * The interface provides means to report error to respective viewModel observers. These errors
     * mainly comprises errors when performing a network request.
     */
    @FunctionalInterface
    public interface ErrorListener {
        void error(@Nullable Throwable throwable, @Nullable String tag);
    }

    public interface Callback {
        void success(String message);

        void failure(String failure);
    }
}
