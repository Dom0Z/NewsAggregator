package fin.ex.newsaggregator;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsaggregator.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    TextView articleHeadlineTV;
    TextView articleDateTV;
    TextView articleAuthorTV;
    ImageView articleImage;
    TextView articleBodyTV;
    TextView articleCountTV;
    public ArticleViewHolder(@NonNull View itemView){
        super(itemView);
        articleHeadlineTV = itemView.findViewById(R.id.articleHeadlineTV);
        articleDateTV= itemView.findViewById(R.id.articleDateTV);
        articleAuthorTV= itemView.findViewById(R.id.articleAuthorTV);
        articleImage= itemView.findViewById(R.id.articleImage);
        articleBodyTV= itemView.findViewById(R.id.articleBodyTV);
        articleBodyTV.setMovementMethod(new ScrollingMovementMethod());
        articleCountTV= itemView.findViewById(R.id.articleCountTV);



    }
}
