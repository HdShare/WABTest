package io.github.wabtest.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout.VERTICAL
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import io.github.wabtest.BuildConfig
import io.github.wabtest.core.expt.ExptManager
import io.github.wabtest.core.test.ConfigItem
import io.github.wabtest.core.test.ConfigOption
import io.github.wabtest.core.test.TestManager
import kotlin.system.exitProcess

class SettingDialog(ctx: Activity) : AlertDialog.Builder(ctx) {

    companion object {
        fun Activity.restartApp() {
            finishAffinity()
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
            exitProcess(0)
        }

        fun show(ctx: Activity) {
            try {
                SettingDialog(ctx).show()
            } catch (e: Exception) {
                AlertDialog.Builder(ctx)
                    .setTitle("发生异常")
                    .setMessage("异常信息:\n$e")
                    .setPositiveButton("重启宿主") { _, _ ->
                        ctx.restartApp()
                    }.show()
            }
        }
    }

    init {
        setTitle(BuildConfig.APP_NAME)
        setView(getContentView(ctx))
        setNeutralButton("删除配置") { _, _ ->
            if (ExptManager.delMmkvFiles()) {
                ctx.restartApp()
            }
        }
        setNegativeButton("返回", null)
        setPositiveButton("重启宿主") { _, _ ->
            ctx.restartApp()
        }
    }

    private fun Context.dp2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun Context.isDarkMode(): Boolean {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun getContentView(ctx: Context): View {
        val dp2 = ctx.dp2px(2f)
        val dp4 = ctx.dp2px(4f)
        val dp8 = ctx.dp2px(8f)
        val dp12 = ctx.dp2px(12f)

        val darkMode = ctx.isDarkMode()
        val surfaceColor = Color.parseColor(if (darkMode) "#0e1415" else "#f5fafb")
        val surfaceContainerColor = Color.parseColor(if (darkMode) "#1b2122" else "#e9eff0")
        val onSurfaceColor = Color.parseColor(if (darkMode) "#dee3e5" else "#171d1e")
        val onSurfaceVariantColor = Color.parseColor(if (darkMode) "#bfc8ca" else "#3f484a")
        val outlineColor = Color.parseColor(if (darkMode) "#899294" else "#6f797a")
        val outlineVariantColor = Color.parseColor(if (darkMode) "#3f484a" else "#bfc8ca")

        fun showPopupMenu(
            view: View,
            options: List<ConfigOption>,
            checkedAlias: String? = null,
            onSelected: (ConfigOption) -> Unit,
        ) {
            PopupMenu(view.context, view).apply {
                gravity = Gravity.END
                val groupId = 100
                options.forEachIndexed { index, option ->
                    menu.add(groupId, index + 1, index, option.alias).isCheckable = true
                }
                menu.setGroupCheckable(groupId, true, true)
                val checkedIndex = options.indexOfFirst { option -> option.alias == checkedAlias }
                if (checkedIndex >= 0) {
                    menu.getItem(checkedIndex).isChecked = true
                }
                setOnMenuItemClickListener { item ->
                    val optionIndex = item.itemId - 1
                    val option = options[optionIndex]
                    item.isChecked = true
                    onSelected(option)
                    true
                }
            }.show()
        }

        fun getGroupView(): LinearLayout {
            return LinearLayout(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                setPadding(dp12, dp8, dp12, dp4)
                addView(TextView(ctx).apply {
                    tag = "group"
                    textSize = 12f
                    setTextColor(onSurfaceVariantColor)
                })
            }
        }

        fun getConfigView(): LinearLayout {
            return LinearLayout(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                setPadding(dp12, dp12, dp12, dp12)
                background = StateListDrawable().apply {
                    addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(outlineVariantColor))
                    addState(intArrayOf(), ColorDrawable(surfaceColor))
                }
                addView(LinearLayout(ctx).apply {
                    layoutParams = LayoutParams(0, WRAP_CONTENT, 1f).apply {
                        gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    }
                    orientation = VERTICAL
                    addView(TextView(ctx).apply {
                        tag = "title"
                        textSize = 13f
                        setTextColor(onSurfaceColor)
                    })
                    addView(TextView(ctx).apply {
                        tag = "desc"
                        textSize = 12f
                        visibility = View.GONE
                        setTextColor(outlineColor)
                    })
                })
                addView(TextView(ctx).apply {
                    layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                        gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    }
                    background = GradientDrawable().apply {
                        setColor(surfaceContainerColor)
                        cornerRadius = dp4.toFloat()
                    }
                    tag = "label"
                    textSize = 12f
                    setTextColor(onSurfaceVariantColor)
                    setPadding(dp4, dp2, dp4, dp2)
                })
            }
        }

        data class ListItem(
            val group: String? = null,
            val config: ConfigItem? = null,
        )

        val testItems = TestManager.getList()
        val listItems = mutableListOf<ListItem>()

        fun updateListItems(query: CharSequence?) {
            val keyword = query?.toString()?.trim().orEmpty()
            listItems.clear()
            testItems.forEach { testItem ->
                val configs = if (keyword.isEmpty() || testItem.group.contains(keyword)) {
                    testItem.configs
                } else {
                    testItem.configs.filter { config ->
                        config.key.contains(keyword) || config.title.contains(keyword) || config.desc.contains(keyword)
                    }
                }
                if (configs.isNotEmpty()) {
                    listItems.add(ListItem(group = testItem.group + "(${configs.size})"))
                    configs.forEach { config ->
                        listItems.add(ListItem(config = config))
                    }
                }
            }
        }

        val listAdapter = object : BaseAdapter() {
            override fun getCount() = listItems.size
            override fun getItem(position: Int) = listItems[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getViewTypeCount() = 2
            override fun getItemViewType(position: Int): Int {
                return if (getItem(position).group != null) 0 else 1
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val item = getItem(position)
                return when (getItemViewType(position)) {
                    0 -> {
                        val group = requireNotNull(item.group)
                        (convertView as? LinearLayout ?: getGroupView()).also { view ->
                            val tvGroup = view.findViewWithTag("group") as? TextView
                            tvGroup?.text = group
                        }
                    }

                    1 -> {
                        val config = requireNotNull(item.config)
                        (convertView as? LinearLayout ?: getConfigView()).also { view ->
                            val tvTitle = view.findViewWithTag("title") as? TextView
                            tvTitle?.text = config.title
                            val tvDesc = view.findViewWithTag("desc") as? TextView
                            if (config.desc.isEmpty()) {
                                tvDesc?.visibility = View.GONE
                            } else {
                                tvDesc?.visibility = View.VISIBLE
                                tvDesc?.text = config.desc
                            }
                            val tvLabel = view.findViewWithTag("label") as? TextView
                            tvLabel?.text = TestManager.getOptionsAlias(config.options, config.key)
                            view.setOnClickListener { row ->
                                val alias = TestManager.getOptionsAlias(config.options, config.key)
                                showPopupMenu(row, config.options, alias) { selectedOption ->
                                    ExptManager.putArgValue(config.key, selectedOption.value)
                                    tvLabel?.text = selectedOption.alias
                                }
                            }
                        }
                    }

                    else -> error("未知的列表项类型")
                }
            }
        }
        updateListItems(null)

        return LinearLayout(ctx).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            orientation = VERTICAL
            setBackgroundColor(surfaceContainerColor)

            addView(LinearLayout(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                    setMargins(dp12, dp12, dp12, dp12)
                }
                setBackgroundColor(surfaceColor)

                val editView = EditText(ctx).apply {
                    layoutParams = LayoutParams(0, MATCH_PARENT, 1f)
                    imeOptions = EditorInfo.IME_ACTION_SEARCH
                    inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    background = null
                    textSize = 13f
                    hint = "搜索..."
                    addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun afterTextChanged(s: Editable?) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            updateListItems(s)
                            listAdapter.notifyDataSetChanged()
                        }
                    })
                }
                addView(editView)

                addView(ImageView(ctx).apply {
                    layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    setPadding(dp4, dp4, dp4, dp4)
                    background = StateListDrawable().apply {
                        addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(outlineVariantColor))
                        addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
                    }
                    isClickable = true
                    setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                    setOnClickListener {
                        editView.setText("")
                    }
                })
            })

            addView(ListView(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, 0, 1f)
                setPadding(dp12, 0, dp12, dp12)
                selector = ColorDrawable(Color.TRANSPARENT)
                divider = ColorDrawable(outlineVariantColor)
                dividerHeight = ctx.dp2px(0.5f)
                adapter = listAdapter
            })
        }
    }
}
