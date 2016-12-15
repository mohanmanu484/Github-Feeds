package com.mohan.location.ydm.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mohan.location.ydm.R;
import com.mohan.location.ydm.model.Event;
import com.mohan.location.ydm.model.RepoDetails;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mohan on 14/12/16.
 */

public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<Event> eventArrayList;
    private Context context;
    private boolean showRepodetails;
    private RepoDetails repoDetails;

    public EventRecyclerAdapter(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }

    public EventRecyclerAdapter(ArrayList<Event> eventArrayList, boolean showRepodetails) {
        this.eventArrayList = eventArrayList;
        this.showRepodetails = showRepodetails;
    }

    public void updateEventList(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
        notifyDataSetChanged();
    }

    public void updateEventList(ArrayList<Event> eventArrayList,RepoDetails repoDetails) {
        this.eventArrayList = eventArrayList;
        this.repoDetails=repoDetails;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_sigle_item_layout,parent,false);
        context=parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        if(showRepodetails){
            if(viewType==0){
                View repoView=LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_details_layout,parent,false);
                viewHolder=new RepoDetailsHolder(repoView);
            }else {
                viewHolder=new EventHolder(view);
            }
        }else {
            viewHolder=new EventHolder(view);
        }

        return viewHolder;
    }

    private void setEventdata(final EventHolder eventHolder, final Event event){
        eventHolder.authorName.setText(event.getAuthor());
        eventHolder.eventAction.setText(event.getAction());
        eventHolder.eventType.setText(event.getEventType());
        String[] dateTime=event.getDateTime().split("T");
        eventHolder.dateTime.setText(getDisplayOnlyDate(dateTime[0]+" "+getDisplayTime(dateTime[1].replace("Z",""))));
        eventHolder.repoName.setText(event.getRepoName());
        eventHolder.actionHolder.setVisibility(event.getAction().isEmpty()?View.GONE:View.VISIBLE);
        Picasso.with(context).load(event.getAvatarUrl()).into(eventHolder.avatar);
        if(!showRepodetails) {
            eventHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("repoURl", event.getRepoUrl());
                    EventsActivity eventsActivity= (EventsActivity) context;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        View view1=eventHolder.avatar;
                        View view2=eventHolder.repoName;
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(eventsActivity, Pair.create(view1, "avatarTransition"),Pair.create(view2, "repotransition"));
                        context.startActivity(intent, options.toBundle());
                    }else {
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int type=  holder.getItemViewType();

        if(showRepodetails) {
            if (type == 0) {

                RepoDetailsHolder repoDetailsHolder = (RepoDetailsHolder) holder;

                repoDetailsHolder.repoName.setText(repoDetails.getName());
                repoDetailsHolder.language.setText(repoDetails.getLanguage());
                repoDetailsHolder.repoUrl.setText(repoDetails.getURL());
                repoDetailsHolder.description.setText(repoDetails.getDescription());
                repoDetailsHolder.watchersCount.setText(""+repoDetails.getWatchCount());
                repoDetailsHolder.starCount.setText(""+repoDetails.getStartCount());
                repoDetailsHolder.forkCount.setText(""+repoDetails.getForkCount());
            } else {
                EventHolder eventHolder = (EventHolder) holder;
                final Event event = eventArrayList.get(position-1);
                setEventdata(eventHolder, event);

            }
        }else {
            EventHolder eventHolder = (EventHolder) holder;
            final Event event = eventArrayList.get(position);
            setEventdata(eventHolder, event);
        }



    }

    @Override
    public int getItemViewType(int position) {

        if(position==0){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return  eventArrayList.size();
    }

    public class RepoDetailsHolder extends RecyclerView.ViewHolder{

        private TextView name,repoName,repoUrl,description,starCount,forkCount,watchersCount,language;
        private View view;
        public RepoDetailsHolder(View itemView) {
            super(itemView);
            view=itemView;
            repoName= (TextView) itemView.findViewById(R.id.tvLibraryName);
            repoUrl= (TextView) itemView.findViewById(R.id.tvGitUrl);
            description= (TextView) itemView.findViewById(R.id.tvDescription);
            starCount= (TextView) itemView.findViewById(R.id.tvStar);
            forkCount= (TextView) itemView.findViewById(R.id.tvForks);
            watchersCount= (TextView) itemView.findViewById(R.id.tvWatchers);
            language= (TextView) itemView.findViewById(R.id.tvLanguage);
        }
    }

    public class EventHolder extends RecyclerView.ViewHolder{

        private TextView authorName;
        private TextView repoName;
        private TextView dateTime;
        private TextView eventType;
        private TextView eventAction;
        private LinearLayout actionHolder;
        private CircleImageView avatar;
        private View view;
        public EventHolder(View itemView) {
            super(itemView);
            view=itemView;
            authorName= (TextView) itemView.findViewById(R.id.tvAuthorName);
            repoName= (TextView) itemView.findViewById(R.id.tvRepoName);
            dateTime= (TextView) itemView.findViewById(R.id.tvDateTime);
            eventType= (TextView) itemView.findViewById(R.id.tvEventType);
            eventAction= (TextView) itemView.findViewById(R.id.tvEventActionName);
            actionHolder= (LinearLayout) itemView.findViewById(R.id.llActionHolder);
            avatar= (CircleImageView) itemView.findViewById(R.id.ivAvatar);
        }
    }

    public static String getDisplayTime(String mysqlDateTime) {
        try {
            SimpleDateFormat curFormater = new SimpleDateFormat(
                    "k:m:s", Locale.US);
            curFormater.setTimeZone(TimeZone.getDefault());
            Date dateObj = curFormater.parse(mysqlDateTime);
            SimpleDateFormat postFormater = new SimpleDateFormat(
                    "h:mm a", Locale.US);
            postFormater.setTimeZone(TimeZone.getDefault());
            String newDateStr = postFormater.format(dateObj);
            return newDateStr;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDisplayOnlyDate(String mysqlDateTime) {
        try {
            SimpleDateFormat curFormater = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.US);
            curFormater.setTimeZone(TimeZone.getDefault());
            Date dateObj = curFormater.parse(mysqlDateTime);
            SimpleDateFormat postFormater = new SimpleDateFormat(
                    "dd MMM yyyy", Locale.US);
            postFormater.setTimeZone(TimeZone.getDefault());
            String newDateStr = postFormater.format(dateObj);
            return newDateStr;
        } catch (Exception e) {
            return "";
        }
    }

}
