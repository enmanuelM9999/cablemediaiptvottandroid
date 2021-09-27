package co.cablebox.tv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import co.cablebox.tv.R;
import co.cablebox.tv.bean.Item;
import co.cablebox.tv.bean.LiveBean;

/* Esta actividad se encarga de organizar la vista de las apliacaciones instalas en el dispositivo
* Esta Actividad solo debe ser usada por administradores que sepan la clave*/
public class AppsListActivity extends Activity {

    private PackageManager manager;
    private List<Item> apps;

    private GridView listApss;

    public static LiveBean liveBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        ButterKnife.bind(this);
        loadApps();
        loadListView();
        addClickListener();
    }

    private void loadApps(){
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri: availableActivities){
            Item app = new Item();

            if(ri.activityInfo.packageName.equals("com.android.tv.settings") || ri.activityInfo.packageName.equals("tv.pluto.android") || ri.activityInfo.packageName.equals("com.anydesk.anydeskandroid") || ri.activityInfo.packageName.equals("com.estrongs.android.pop")){
                app.setLabel(ri.activityInfo.packageName);
                app.setName(ri.loadLabel(manager));
                app.setIcon(ri.loadIcon(manager));
                apps.add(app);
            }
        }
    }

    private void loadListView(){
        listApss = (GridView) findViewById(R.id.list_panel_app);

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, R.layout.item, apps){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.row_item, null);
                }

                ImageView appIcon = (ImageView) convertView.findViewById(R.id.image_app);
                appIcon.setImageDrawable(apps.get(position).getIcon());

                TextView appName = (TextView) convertView.findViewById(R.id.name_app);
                appName.setText(apps.get(position).getName());

                return convertView;
            }
        };

        listApss.setAdapter(adapter);
    }

    private  void addClickListener(){
        listApss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).getLabel().toString());
                startActivity(i);
            }
        });
    }

    public static void openLive(Context context, LiveBean liveBean) {
        AppsListActivity.liveBean = liveBean;
        context.startActivity(new Intent(context, AppsListActivity.class));
    }

    @Override
    public void onBackPressed() {
        ServiceProgramActivity.openLive(this);
        finish();
    }
}
