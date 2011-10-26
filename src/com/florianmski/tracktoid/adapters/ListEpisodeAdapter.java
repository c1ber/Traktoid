/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ListEpisodeAdapter extends BaseAdapter 
{
	private List<TvShowEpisode> episodes;
	private Context context;
	private boolean watchedMode = false;
	private String tvdb_id;
	private Map<Integer, Boolean> listWatched = new HashMap<Integer, Boolean>();
	private Bitmap placeholder;
	
    public ListEpisodeAdapter(List<TvShowEpisode> episodes, Context context, String tvdb_id)
    {
    	this.episodes = episodes;
    	this.context = context;
    	this.tvdb_id = tvdb_id;
    	placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
    }
	
    //set or unset mode where user can check watched episodes
    public void setWatchedMode(boolean watchedMode)
    {
    	this.watchedMode = watchedMode;
    	
    	if(watchedMode)
    		listWatched.clear();
    }
    
    //check or uncheck all episodes of a season
    public void checkBoxSelection(boolean checked)
	{
    	listWatched.clear();
    	for(TvShowEpisode e : episodes)
    	{
    		if(e.getWatched() != checked)
    			listWatched.put(e.getNumber(), checked);
    	}
	}
    
    public void reloadData(List<TvShowEpisode> episodes)
	{
    	this.episodes = episodes;
	}
    
    public List<TvShowEpisode> getEpisodes()
    {
    	return episodes;
    }
    
    public Map<Integer, Boolean> getListWatched()
    {
    	return listWatched;
    }
    
    @Override
    public int getCount() 
    {
        return episodes.size();
    }

    @Override
    public Object getItem(int position) 
    {
        return null;
    }

    @Override
    public long getItemId(int position) 
    {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) 
    {
    	final ViewHolder holder;

        if (convertView == null) 
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_episode, parent, false);
            holder = new ViewHolder();
            holder.ivScreen = (ImageView)convertView.findViewById(R.id.imageViewScreen);
            holder.tvTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
            holder.tvEpisode = (TextView)convertView.findViewById(R.id.textViewEpisode);
            holder.cbWatched = (CheckBox)convertView.findViewById(R.id.checkBoxWatched);
            holder.llWatched = (LinearLayout)convertView.findViewById(R.id.linearLayoutWatched);
            holder.ivBandeau = (ImageView)convertView.findViewById(R.id.imageViewBadge);
            
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        
        TvShowEpisode e = episodes.get(position);
        
        Image i = new Image(tvdb_id, e.getImages().getScreen(), e.getSeason(), e.getNumber());
        AQuery aq = new AQuery(convertView);
        //in case user scroll the list fast, stop loading images from web
        if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
            aq.id(holder.ivScreen).image(placeholder);
        else
        	aq.id(holder.ivScreen).image(i.getUrl(), true, false, 0, 0, placeholder, android.R.anim.fade_in, 9.0f / 16.0f);
        
        holder.tvTitle.setText(e.getTitle());
        holder.tvEpisode.setText("Episode " + e.getNumber());
        
        if(watchedMode)
        {       	
        	holder.llWatched.setVisibility(View.VISIBLE);
        	holder.cbWatched.setOnClickListener(new OnClickListener() 
        	{
				@Override
				public void onClick(View v)
				{
					if(watchedMode)
					{
						int episode = episodes.get(position).getNumber();
						boolean watched = episodes.get(position).getWatched();
						//it means that episode has been checked twice so user has changed his mind
						if(listWatched.containsKey(episode) || (watched == holder.cbWatched.isChecked()))
							listWatched.remove(episode);
						else
							listWatched.put(episode, holder.cbWatched.isChecked());
					}
				}
			});
            
            if(listWatched.containsKey(e.getNumber()))
        		holder.cbWatched.setChecked(listWatched.get(e.getNumber()));
        	else
        		holder.cbWatched.setChecked(e.getWatched());
            
        }
        else
        {
        	holder.llWatched.setVisibility(View.GONE);
        	
            holder.cbWatched.setChecked(e.getWatched());
        }
        
        if(e.getWatched())
        	holder.ivBandeau.setVisibility(View.VISIBLE);
        else
        	holder.ivBandeau.setVisibility(View.GONE);
		
        return convertView;
    }
    
    private static class ViewHolder 
    {
    	private ImageView ivScreen;
    	private TextView tvTitle;
    	private TextView tvEpisode;
    	private CheckBox cbWatched;
    	private LinearLayout llWatched;
    	private ImageView ivBandeau;
    }

}
