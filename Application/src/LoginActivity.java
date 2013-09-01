package com.delivery.assistant;

public class LoginActivity extends Activity {
    private TextView ChangePassword;
    private EditText InputUserName;
    private EditText InputPassword;
    private Button LoginButton;
    private String username;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    	setContentView(R.layout.login);

    	InputUserName=(EditText)findViewById(R.id.login_username);
	InputPassword=(EditText)findViewById(R.id.login_password);
    	TextView ChangePassword = (TextView) findViewById(R.id.change_password);

	LoginButton = (Button) findViewById(R.id.btnLogin);
	OnClickListener ButtonListener = new OnClickListener() {
	    public void onClick(View v) {
		username = InputUserName.getText().toString();
		password = InputPassword.getText().toString();

		if(username.equals("")||password.equals("")){
		    Toast.makeText(getApplicationContext(), "Required field missing!",
			    Toast.LENGTH_LONG).show();
		    return;
		}
		
		// If username does exists
		if(loginDataBaseAdapter.existOrNot(username)){
		    String storedPassword=loginDataBaseAdapter.getSinlgeEntry(username);
		    if(password.equals(storedPassword)) {
			Toast.makeText(LoginActivity.this,
				"Login Successfull", Toast.LENGTH_LONG).show();
			Intent a = new Intent(getApplicationContext(), Assistant.class);
			startActivity(a);
		    } else {
			Toast.makeText(LoginActivity.this,
				"User Name or Password does not match",
				Toast.LENGTH_LONG).show();
		    }
		}
	    }
	};
	LoginButton.setOnClickListener(ButtonListener);

    	ChangePassword.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View v) {
    	        Intent i = new Intent(getApplicationContext(), PasswordActivity.class);
    	        startActivity(i);
    	    }
    	});
    }
}
