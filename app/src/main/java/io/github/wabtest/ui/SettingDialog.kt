package io.github.wabtest.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import android.view.Window
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
import android.widget.Switch
import android.widget.TextView
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import io.github.wabtest.BuildConfig
import io.github.wabtest.R
import io.github.wabtest.core.expt.ExptManager
import io.github.wabtest.core.test.ConfigItem
import io.github.wabtest.core.test.ConfigOption
import io.github.wabtest.core.test.ConfigType
import io.github.wabtest.core.test.TestManager
import kotlin.system.exitProcess

class SettingDialog(private val ctx: Activity) : Dialog(ctx) {

    companion object {
        fun Activity.restartApp() {
            finishAffinity()
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
            exitProcess(0)
        }

        fun show(ctx: Activity) {
            try {
                SettingDialog(ctx.apply { injectModuleAppResources() }).show()
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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(getContentView(ctx))
        window?.apply {
            val metrics = ctx.resources.displayMetrics
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
            setWindowAnimations(0)
            setLayout(
                minOf(metrics.widthPixels - ctx.dp2px(32f), ctx.dp2px(560f)),
                minOf(metrics.heightPixels - ctx.dp2px(32f), ctx.dp2px(800f)),
            )
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

    private fun getContentView(ctx: Activity): View {
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

        fun showActionMenu(view: View, onEnableAll: () -> Unit) {
            PopupMenu(view.context, view).apply {
                gravity = Gravity.END
                menu.add(0, 1, 0, "一键开启")
                menu.add(0, 2, 1, "删除配置")
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> {
                            onEnableAll()
                            true
                        }

                        2 -> {
                            if (ExptManager.delMmkvFiles()) {
                                ctx.restartApp()
                            } else {
                                dismiss()
                            }
                            true
                        }

                        else -> false
                    }
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
                setPadding(dp12, dp8, dp12, dp8)
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
                    tag = "label"
                    textSize = 12f
                    setTextColor(onSurfaceVariantColor)
                })
                addView(ImageView(ctx).apply {
                    layoutParams = LayoutParams(ctx.dp2px(24f), ctx.dp2px(24f)).apply {
                        gravity = Gravity.END or Gravity.CENTER_VERTICAL
                        setMargins(dp4, 0, 0, 0)
                    }
                    tag = "arrow"
                    visibility = View.GONE
                    setImageResource(R.drawable.ic_right)
                    setColorFilter(onSurfaceVariantColor)
                })
                addView(Switch(ctx).apply {
                    layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                        gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    }
                    tag = "switch"
                    visibility = View.GONE
                    showText = false
                })
            }
        }

        fun getConfigBackground(isFirstInGroup: Boolean, isLastInGroup: Boolean): StateListDrawable {
            val topRadius = if (isFirstInGroup) dp8.toFloat() else 0f
            val bottomRadius = if (isLastInGroup) dp8.toFloat() else 0f
            val radii = floatArrayOf(
                topRadius, topRadius,
                topRadius, topRadius,
                bottomRadius, bottomRadius,
                bottomRadius, bottomRadius,
            )

            fun createDrawable(color: Int) = GradientDrawable().apply {
                setColor(color)
                cornerRadii = radii
            }

            return StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), createDrawable(outlineVariantColor))
                addState(intArrayOf(), createDrawable(surfaceColor))
            }
        }

        fun getActionButton(label: String, onClick: () -> Unit): TextView {
            return TextView(ctx).apply {
                layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                minWidth = ctx.dp2px(64f)
                gravity = Gravity.CENTER
                text = label
                textSize = 13f
                setTextColor(onSurfaceColor)
                setPadding(dp12, dp8, dp12, dp8)
                background = StateListDrawable().apply {
                    addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(outlineVariantColor))
                    addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
                }
                isClickable = true
                isFocusable = true
                setOnClickListener { onClick() }
            }
        }

        data class ListItem(
            val group: String? = null,
            val config: ConfigItem? = null,
            val isFirstInGroup: Boolean = false,
            val isLastInGroup: Boolean = false,
        )

        val testItems = TestManager.getList()
        val listItems = mutableListOf<ListItem>()

        fun updateListItems(query: CharSequence?) {
            val keyword = query?.toString()?.trim().orEmpty()
            listItems.clear()
            testItems.forEach { testItem ->
                val configs = if (keyword.isEmpty() || testItem.group.title.contains(keyword)) {
                    testItem.configs
                } else {
                    testItem.configs.filter { config ->
                        config.key.contains(keyword) || config.title.contains(keyword) || config.desc.contains(keyword)
                    }
                }
                if (configs.isNotEmpty()) {
                    listItems.add(ListItem(group = testItem.group.title + "(${configs.size})"))
                    configs.forEachIndexed { index, config ->
                        listItems.add(
                            ListItem(
                                config = config,
                                isFirstInGroup = index == 0,
                                isLastInGroup = index == configs.lastIndex,
                            )
                        )
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
                            view.background = getConfigBackground(item.isFirstInGroup, item.isLastInGroup)
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
                            val arrowView = view.findViewWithTag("arrow") as? ImageView
                            val switchView = view.findViewWithTag("switch") as? Switch
                            val currentValue = ExptManager.getArgValue(config.key)
                            val alias = TestManager.getOptionAlias(config.options, currentValue)
                            view.alpha = if (currentValue == null) 0.6f else 1f

                            when (config.type) {
                                ConfigType.SWITCH -> {
                                    val offOption = config.options.getOrNull(0)
                                    val onOption = config.options.getOrNull(1)
                                    tvLabel?.visibility = View.GONE
                                    arrowView?.visibility = View.GONE
                                    switchView?.visibility = View.VISIBLE
                                    switchView?.setOnCheckedChangeListener(null)
                                    switchView?.isChecked = currentValue == onOption?.value
                                    switchView?.setOnCheckedChangeListener { _, isChecked ->
                                        val selectedOption = if (isChecked) onOption else offOption
                                        selectedOption?.let {
                                            ExptManager.putArgValue(config.key, it.value)
                                            view.alpha = 1f
                                        }
                                    }
                                    view.setOnClickListener { switchView?.performClick() }
                                }

                                ConfigType.SINGLE_CHOICE -> {
                                    switchView?.setOnCheckedChangeListener(null)
                                    switchView?.visibility = View.GONE
                                    tvLabel?.visibility = if (alias == null) View.GONE else View.VISIBLE
                                    tvLabel?.text = alias.orEmpty()
                                    arrowView?.visibility = View.VISIBLE
                                    view.setOnClickListener { row ->
                                        val value = ExptManager.getArgValue(config.key)
                                        val checkedAlias = TestManager.getOptionAlias(config.options, value)
                                        showPopupMenu(row, config.options, checkedAlias) { selectedOption ->
                                            ExptManager.putArgValue(config.key, selectedOption.value)
                                            view.alpha = 1f
                                            tvLabel?.visibility = View.VISIBLE
                                            tvLabel?.text = selectedOption.alias
                                        }
                                    }
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
            background = GradientDrawable().apply {
                setColor(surfaceColor)
                cornerRadius = dp8.toFloat()
            }
            clipToOutline = true

            addView(LinearLayout(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, ctx.dp2px(52f))
                gravity = Gravity.CENTER_VERTICAL
                setBackgroundColor(surfaceColor)

                addView(TextView(ctx).apply {
                    layoutParams = LayoutParams(0, MATCH_PARENT, 1f)
                    gravity = Gravity.CENTER_VERTICAL
                    text = BuildConfig.APP_NAME
                    textSize = 18f
                    setTextColor(onSurfaceColor)
                    setPadding(dp12, 0, dp12, 0)
                })

                addView(ImageView(ctx).apply {
                    layoutParams = LayoutParams(ctx.dp2px(32f), ctx.dp2px(32f)).apply {
                        setMargins(0, 0, dp4, 0)
                    }
                    contentDescription = "更多操作"
                    setPadding(dp4, dp4, dp4, dp4)
                    setImageResource(R.drawable.ic_menu)
                    setColorFilter(onSurfaceVariantColor)
                    background = StateListDrawable().apply {
                        addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(outlineVariantColor))
                        addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
                    }
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { anchor ->
                        showActionMenu(anchor) {
                            testItems.asSequence()
                                .flatMap { it.configs.asSequence() }
                                .filter { it.type == ConfigType.SWITCH }
                                .forEach { config ->
                                    config.options.getOrNull(1)?.let { option ->
                                        ExptManager.putArgValue(config.key, option.value)
                                    }
                                }
                            listAdapter.notifyDataSetChanged()
                        }
                    }
                })
            })

            addView(LinearLayout(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, 0, 1f)
                orientation = VERTICAL
                setBackgroundColor(surfaceContainerColor)

                addView(LinearLayout(ctx).apply {
                    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                        setMargins(dp12, dp12, dp12, dp12)
                    }
                    background = GradientDrawable().apply {
                        setColor(surfaceColor)
                        cornerRadius = dp8.toFloat()
                    }
                    clipToOutline = true

                    val editView = EditText(ctx).apply {
                        layoutParams = LayoutParams(0, MATCH_PARENT, 1f)
                        imeOptions = EditorInfo.IME_ACTION_SEARCH
                        inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                        background = null
                        textSize = 13f
                        hint = "搜索..."
                        setTextColor(onSurfaceColor)
                        setHintTextColor(outlineColor)
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
                        layoutParams = LayoutParams(ctx.dp2px(32f), ctx.dp2px(32f)).apply {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                        contentDescription = "清除搜索"
                        setPadding(dp4, dp4, dp4, dp4)
                        background = StateListDrawable().apply {
                            addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(outlineVariantColor))
                            addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
                        }
                        isClickable = true
                        isFocusable = true
                        setImageResource(R.drawable.ic_close)
                        setColorFilter(onSurfaceVariantColor)
                        setOnClickListener {
                            editView.setText("")
                        }
                    })
                })

                addView(ListView(ctx).apply {
                    layoutParams = LayoutParams(MATCH_PARENT, 0, 1f)
                    setPadding(dp12, 0, dp12, dp12)
                    selector = ColorDrawable(Color.TRANSPARENT)
                    divider = ColorDrawable(surfaceContainerColor)
                    dividerHeight = ctx.dp2px(1f)
                    adapter = listAdapter
                })
            })

            addView(LinearLayout(ctx).apply {
                layoutParams = LayoutParams(MATCH_PARENT, ctx.dp2px(52f))
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                setBackgroundColor(surfaceColor)

                addView(getActionButton("返回") { dismiss() })
                addView(getActionButton("重启宿主") { ctx.restartApp() })
            })
        }
    }
}
