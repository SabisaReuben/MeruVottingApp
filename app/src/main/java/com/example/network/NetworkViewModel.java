package com.example.network;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class NetworkViewModel extends AndroidViewModel {
    private static final String TAG = NetworkViewModel.class.getName();
    /**update respective observers on failure **/
    private Callback callback;
    /**holds the list of all registered voters*/
    private MutableLiveData<List<Voter>> voterListLiveData = new MutableLiveData<>();
    /**hold temporary info of a user**/
    private MutableLiveData<Voter> voterSpecificLiveData = new MutableLiveData<>();
    /**holds list of all registered candidates **/
    private MutableLiveData<List<Candidate>> registeredCandidatesLiveData = new MutableLiveData<>();
    /**holds candidate for specific school. Determined by logged in user's school id*/
    private MutableLiveData<List<Candidate>> candidatesLiveData = new MutableLiveData<>();

    private MutableLiveData<Timer> timeRemainingLiveData = new MutableLiveData<>();
    private MutableLiveData<Message> responseMessageLiveData = new MutableLiveData<>();
    private MutableLiveData<Message> timerResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<ClockTimerFragment.ClockTimer> clockTimerMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Statistics> statisticsLiveData = new MutableLiveData<>();

    public MutableLiveData<Statistics> getStatisticsLiveData() {
        return statisticsLiveData;
    }
    public  void getStatistics(){
        dataApi.getStatistics().enqueue(new retrofit2.Callback<Statistics>() {
            @Override
            public void onResponse(@NonNull Call<Statistics> call, @NonNull Response<Statistics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statisticsLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Statistics> call,@NonNull Throwable t) {
                if(callback!=null) callback.failure(t,"statistics");
            }
        });
    }

    public MutableLiveData<ClockTimerFragment.ClockTimer> getClockTimerMutableLiveData() {
        return clockTimerMutableLiveData;
    }

    public void refreshTime() {
        dataApi.getTime("fetch").enqueue(new retrofit2.Callback<ClockTimerFragment.ClockTimer>() {
            @Override
            public void onResponse(@NonNull Call<ClockTimerFragment.ClockTimer> call, @NonNull Response<ClockTimerFragment.ClockTimer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clockTimerMutableLiveData.postValue(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ClockTimerFragment.ClockTimer> call,@NonNull Throwable t) {
                if(callback!=null) callback.failure(t,"timerClock1");
            }
        });
    }

    public MutableLiveData<Message> getTimerResponseLiveData() {
        return timerResponseLiveData;
    }

    public void setClockTimer(Map<String, String> map) {
        dataApi.setTimer(map).enqueue(new retrofit2.Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    timerResponseLiveData.postValue(response.body());

                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if(callback!=null) callback.failure(t,"timerClock");
            }
        });
    }

    public MutableLiveData<Message> getResponseMessageLiveData() {
        return responseMessageLiveData;
    }

    public MutableLiveData<Timer> getTimeRemainingLiveData() {
        return timeRemainingLiveData;
    }

    public void getRemainingTime(String activity) {
        dataApi.getTimeRemaining(activity).enqueue(new retrofit2.Callback<Timer >() {
            @Override
            public void onResponse(@NonNull Call<Timer > call, @NonNull Response<Timer > response) {
                if(response.isSuccessful() && response.body()!=null)
                    timeRemainingLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Timer > call, @NonNull Throwable t) {
                Log.d("tag", t.getMessage() + "resulting from getVoteResults ");
                if(callback!=null) callback.failure(t, "timeRemaining");
            }
        });
    }
    /**holds the result of an election*/
    private MutableLiveData<List<ViewResultFragment.Result>> electionResultLiveData =
            new MutableLiveData<>();
    private DataApi dataApi;
    public NetworkViewModel(@NonNull Application application) {
        super(application);
        dataApi = NetworkService.createService(DataApi.class, application);

    }

    public MutableLiveData<List<ViewResultFragment.Result>> getElectionResultLiveData() {
        return electionResultLiveData;
    }

    public MutableLiveData<Voter> getVoterSpecificLiveData() {
        return voterSpecificLiveData;
    }

    /**
     * Registers fragment to receive failure updates
     * @param callback Callback to be registered.
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * The method fetches list of all registered voters. Fragment or activity can receive update
     * through the viewModel
     * @see this#getVoterListLiveData()
     */
    public void refreshVoterList(){
        dataApi.getVoterList().enqueue(new retrofit2.Callback<List<Voter>>() {
            @Override
            public void onResponse(@NonNull Call<List<Voter>> call, @NonNull Response<List<Voter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, response.body() + "voter list");
                    voterListLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Voter>> call, @NonNull Throwable t) {
                Log.d("tag", t.getMessage() + "resulting from getVoteResults ");
                if(callback!=null) callback.failure(t,"voterList");
            }
        });
    }

    public MutableLiveData<List<Voter>> getVoterListLiveData() {
        return voterListLiveData;
    }

    public MutableLiveData<List<Candidate>> getRegisteredCandidatesLiveData() {
        return registeredCandidatesLiveData;
    }

    /**
     * Holds list of candidate of a given school that user can vote.
     * The list can be empty if election has not started or no candidates available.
     * @return MutableLiveData
     * @see this#getCandidates(String)
     */
    public MutableLiveData<List<Candidate>> getCandidatesLiveData() {
        return candidatesLiveData;
    }

    /**
     * The method fetches all candidates of a given school
     * @param school The schoolId of a logged in user
     */
    public void getCandidates(String school) {
        dataApi.getCandidateList(school).enqueue(new retrofit2.Callback<List<Candidate>>() {
            @Override
            public void onResponse(@NonNull Call<List<Candidate>> call, @NonNull Response<List<Candidate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    candidatesLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Candidate>> call, @NonNull Throwable t) {
                Log.d("tag", t.getMessage() + "resulting from getVoteResults ");
                if(callback!=null) callback.failure(t,"voteFragment");
            }
        });
    }

    public void refreshCandidateList(){
        dataApi.getAllCandidates().enqueue(new retrofit2.Callback<List<Candidate>>() {
            @Override
            public void onResponse(@NonNull Call<List<Candidate>> call, @NonNull Response<List<Candidate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    registeredCandidatesLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Candidate>> call, @NonNull Throwable t) {
                Log.d("tag", t.getMessage() + "resulting from getVoteResults ");
                if(callback!=null) callback.failure(t,"candidateRegister");
            }
        });

    }
    public void getVoter(String schoolId){
        dataApi.getVoter(schoolId).enqueue(new retrofit2.Callback<Voter>() {
            @Override
            public void onResponse(@NonNull Call<Voter> call, @NonNull Response<Voter> response) {
                if (response.isSuccessful() && response.body() != null) {
                    voterSpecificLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Voter> call, @NonNull Throwable t) {
                Log.d("tag", t.getMessage() + "resulting from getVoteResults ");
                if(callback!=null) callback.failure(t, "profileFragment");
            }
        });
    }

    public void submitVoters(List<Candidate> candidates ,String schoolId) {
        Map<String, String> votesMap = getVotesMap(candidates);
        votesMap.put("school_id", schoolId);
        dataApi.vote(votesMap).enqueue(new retrofit2.Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.body() != null && response.isSuccessful()) {
                    responseMessageLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if(callback!=null) callback.failure(t,"submitVote");

            }
        });
    }

    private Map<String, String> getVotesMap(List<Candidate> list) {
        Map<String, String> map = new HashMap<>();
        for (Candidate candidate : list) {
            switch (candidate.getPositionVying()) {
                case "President":
                    map.put("President", candidate.getVoterName());
                    break;
                case  "Women Representative":
                    map.put("Women", candidate.getVoterName());
                    break;
                case "Men Representative":
                    map.put("Men", candidate.getVoterName());
                    break;
                case "School Representative":
                    map.put("School", candidate.getVoterName());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + candidate.getPositionVying());
            }
        }
        return map;
    }

    public  interface Callback {
        void failure(Throwable t, String tag);
    }

    public void getVoteResults() {
        dataApi.getResult().enqueue(new retrofit2.Callback<List<ViewResultFragment.Result>>() {
            @Override
            public void onResponse(@NonNull Call<List<ViewResultFragment.Result>> call,
                                   @NonNull Response<List<ViewResultFragment.Result>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    electionResultLiveData.postValue(response.body());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<ViewResultFragment.Result>> call,
                                  @NonNull Throwable t) {
                Log.d("tag", t.getMessage() + "resulting from getVoteResults ");
                if(callback!=null) callback.failure(t, "viewResult");

            }
        });
    }


}
