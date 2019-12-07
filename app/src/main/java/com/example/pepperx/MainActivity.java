package com.example.pepperx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionImportance;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionValidity;
import com.aldebaran.qi.sdk.object.conversation.Bookmark;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Chatbot;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private Topic topic;
    private QiChatbot chatbot;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.initscreen);


        QiSDK.register(this, this);

    }


    @Override
    public void onRobotFocusGained(final QiContext qiContext) {

        this.qiContext = qiContext;

        Say say = SayBuilder.with(qiContext) // Create the builder with the context.
                .withText("Salve, Fremder.\n" +
                        "Kannst du mir bitte bitte helfen die Fibel für meinen Papa zu finden?\n" +
                        "Weil der braucht die um seinen Mantel zu schließen und den braucht er doch für seine Arbeit als Beamter. \n" +
                        "Wenn ich die nicht finde muss ich ganz viele langweilige Konjugationen auf meine Wachstafel schreiben.\n" +
                        "\n" +
                        "Hilfst du mir bitte") // Set the text to say.
                .build(); // Build the say action.
        say.run();
        triggerAnimation(qiContext, R.raw.confused_a001);

        topic = TopicBuilder.with(qiContext).withResource(R.raw.quest_roemer).build();
        chatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();


        chatbot.addOnBookmarkReachedListener(new QiChatbot.OnBookmarkReachedListener() {
            @Override
            public void onBookmarkReached(Bookmark bookmark) {
                switch (bookmark.getName()) {
                    case "Bookmark1":
                        triggerAnimation(qiContext, R.raw.question_both_hand_a003);
                        break;
                    case "drawBookmark":
                        updateBackgroundImg("wachs");
                        triggerAnimation(qiContext, R.raw.show_body_a001);
                        break;
                    case "startQuestBookmark":
                        updateBackgroundImg("scan");
                        triggerAnimation(qiContext, R.raw.right_hand_high_b001);
                        break;
                }
            }
        });


        Chat chat = ChatBuilder.with(qiContext).withChatbot(chatbot).build();
        chat.async().run();


    }


    public void triggerAnimation(QiContext qiContext, int raw) {
        Animation animation = AnimationBuilder.with(qiContext).withResources(raw).build();
        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
        animate.async().run();
    }

    public void updateBackgroundImg(String img) {
        switch (img) {
            case "wachs":
                runOnUiThread(() -> {
                    layout.setBackgroundResource(R.drawable.pferdefibel);
                });
                break;
            case "scan":
                runOnUiThread(() -> {
                    layout.setBackgroundResource(R.drawable.scan);
                });
                break;
        }
    }

    public void goToMyBookmark(String bookmarkName) {
        chatbot.async().goToBookmark(topic.getBookmarks().get(bookmarkName), AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
}
