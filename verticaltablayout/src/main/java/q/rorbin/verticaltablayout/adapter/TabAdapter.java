package q.rorbin.verticaltablayout.adapter;



import java.util.List;
import java.util.Map;

import q.rorbin.verticaltablayout.widget.TabView;

/**
 * @author chqiu
 *         Email:qstumn@163.com
 */
public interface TabAdapter {
    int getCount();

    TabView.TabBadge getBadge(int position);

    TabView.TabIcon getIcon(int position);
    List<Map> getList();

    TabView.TabTitle getTitle(int position);



    int getBackground(int position);
}
