package fish.eyed.sleepy.todo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import fish.eyed.sleepy.todo.fragments.TodoEditFragment
import fish.eyed.sleepy.todo.fragments.TodoMainFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    TodoMainFragment.OnFragmentInteractionListener,
    TodoEditFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        savedInstanceState?.let {
            return
        }

        // メインコンテンツ
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_container, TodoMainFragment.newInstance())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
//            R.id.action_complete -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFragmentInteraction(fragment: TodoMainFragment) {
        // TODO
    }

    override fun onFragmentInteraction(fragment: TodoEditFragment) {
        // TODO
    }

}
