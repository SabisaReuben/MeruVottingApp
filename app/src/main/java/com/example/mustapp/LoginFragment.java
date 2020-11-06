package com.example.mustapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.mustapp.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    /**
     * Bundle key to persist progress state. The status is represnted in integer format.
     * 1 rep. visible state otherwise 0.
     */
    private static final String PROGRESSBAR_STATUS_KEY = "progress";
    /**
     * ViewModel containing all validation logic
     */
    private ValidationViewModel viewModel;

    /**
     * Indicator for progress or network request
     */
    private ProgressBar progressBar;
    private String role = "Voter";
    private static final String ROLE_KEY = "role_key";
    private static String PASSWORD_KEY = "password";
    private static String SCHOOL_ID_KEY = "school";

    private OnBackPressedCallback onBackPressedCallback;

    private FirebaseAuth firebaseAuth;

    private NavController navController;
    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        updateUI(firebaseAuth.getCurrentUser());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity())
                .get(ValidationViewModel.class);
        FragmentLoginBinding binding = FragmentLoginBinding.inflate(inflater, container,
                false);
        navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
        binding.setFragmentLogin(this);
        //set a layout binding
        viewModel.setLoginLayoutBinding(binding);
        viewModel.getLoginLayoutValidator().observe(requireActivity(), login -> bind(login, binding));
        //receive any error updates
        progressBar = binding.progressLogin;
        //restore progressbar state
        if (savedInstanceState != null) {
            progressBar.setVisibility(savedInstanceState.getInt(PROGRESSBAR_STATUS_KEY));
            role = savedInstanceState.getString(ROLE_KEY);
        }

        TextView forgotText = binding.forgotPassword;
        SpannableString spannableString = new SpannableString(forgotText.getText());
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

            }
        }, 23, forgotText.getText().length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new UnderlineSpan() {
                                }, 23, forgotText.getText().length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        forgotText.setText(spannableString, TextView.BufferType.SPANNABLE);
        return binding.getRoot();

    }

    private void bind(Login login, FragmentLoginBinding binding) {
        if(getActivity()!=null){
        SharedPreferences sharedPreference = requireContext().getSharedPreferences(
                "credentials", Context.MODE_PRIVATE);
        login.emailLogin = sharedPreference.getString(SCHOOL_ID_KEY, "CT206/009/78");
        login.loginPassword = sharedPreference.getString(PASSWORD_KEY, "******");
        }
        binding.setLogin(login);
    }

    /**
     * Called once the user clicks Login button
     *
     * @param view  The view clicked
     * @param login {@link Login}
     */
    @SuppressWarnings("unused")
    public void login(View view, Login login) {
        role = login.getRole();
        if (login.isRememberChecked()) {
            SharedPreferences preferences = view.getContext().getSharedPreferences("credentials",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(PASSWORD_KEY, login.loginPassword);
            edit.putString(SCHOOL_ID_KEY, login.emailLogin);
            edit.apply();
        }
        progressBar.setVisibility(android.view.View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(login.getEmailLogin(), login.getLoginPassword())
                .addOnSuccessListener(authResult -> updateUI(authResult.getUser()))
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void startRegistration(View view) {
        navController.navigate(R.id.action_loginFragment_to_registrationFragment, null,
                getNavOptions(R.id.registrationFragment));
    }

    public static class Login extends BaseObservable {
        /**
         * The layout binding object.
         * Use it to pass errors to the interface
         */
        @SuppressWarnings({"unused"})
        protected transient FragmentLoginBinding binding;
        /**
         * String entered by user on a password layout
         */
        private String loginPassword;
        /**
         * The String representing SchoolID filled by user on input box
         */
        private String emailLogin;
        /**
         * The role of entity requesting authentication. The defined roles include voter,admin and
         * candidate;
         */
        private String role;
        /**
         * State of the checkbox. True for checked otherwise false.
         */
        private boolean rememberChecked;

        /**
         * default constructor. Initializes the variables for validation
         */
        @SuppressWarnings("WeakerAccess")
        public Login() {
            this.emailLogin = loginPassword = "";
            role = "Voter";
            rememberChecked = true;
        }

        /**
         * Use the method to initialize the layout binding
         *
         * @param binding Layout binding object
         */
        @SuppressWarnings("unused")
        public void setLayout(@Nullable FragmentLoginBinding binding) {
            this.binding = binding;
        }


        @Bindable
        public String getLoginPassword() {
            return loginPassword;
        }

        @Bindable
        @SuppressWarnings("unused")
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            //avoid the infinite loop
            if (!this.role.equals(role)) {
                this.role = role;
                notifyPropertyChanged(BR.role);
            }
        }

        public void setLoginPassword(String loginPassword) {
            if (!this.loginPassword.equals(loginPassword)) {
                this.loginPassword = loginPassword;
                notifyPropertyChanged(BR.loginPassword);
            }

        }

        @Bindable
        public boolean isRememberChecked() {
            return rememberChecked;
        }

        public void setRememberChecked(boolean rememberChecked) {
            if (this.rememberChecked != rememberChecked) {
                this.rememberChecked = rememberChecked;
                notifyPropertyChanged(BR.rememberChecked);
            }
        }

        @Bindable
        public String getEmailLogin() {
            return emailLogin;
        }

        @SuppressWarnings("unused")
        public void setEmailLogin(String emailLogin) {
            if (!this.emailLogin.equals(emailLogin)) {
                this.emailLogin = emailLogin;
                notifyPropertyChanged(BR.emailLogin);
            }
        }

        @NonNull
        @Override
        public String toString() {
            return "Login{" +
                    "binding=" + binding +
                    ", loginPassword='" + loginPassword + '\'' +
                    ", schoolIdLogin='" + emailLogin + '\'' +
                    ", role='" + role + '\'' +
                    ", rememberChecked=" + rememberChecked +
                    '}';
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (progressBar != null) outState.putInt(PROGRESSBAR_STATUS_KEY, progressBar.getVisibility());
        outState.putString(ROLE_KEY, role);
        super.onSaveInstanceState(outState);
    }

    private static NavOptions getNavOptions(@IdRes int popUpTo) {
        return new NavOptions.Builder().
                setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopUpTo(popUpTo, true)
                .setPopEnterAnim(android.R.anim.slide_in_left)
                .setPopExitAnim(android.R.anim.slide_out_right)
                .build();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onBackPressedCallback=new OnBackPressedCallback(
                true) {
            @Override
            public void handleOnBackPressed() {
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((onBackPressedCallback!=null)) {
            onBackPressedCallback.setEnabled(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
        }
    }

    private void updateUI(FirebaseUser user) {

    }
}

