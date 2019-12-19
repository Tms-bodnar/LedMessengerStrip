package dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kalandlabor.ledmessengerstrip.R;

public class NewMessageDialog extends Dialog implements View.OnClickListener {

    Context c;
    Button save;

    public NewMessageDialog(@NonNull Context context) {
        super(context);
        c = context;
    }

    public NewMessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected NewMessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_message_dialog);
        save = (Button) findViewById(R.id.save_new_message);
        save.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

    }
}
