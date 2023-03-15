package com.example.mt.ui.view

//
//class TextListener : TextWatcher {
//    private var _before: String? = null
//    private var _old: String? = null
//    private var _new: String? = null
//    private var _after: String? = null
//
//    private var _ignore: Boolean = false
//
//    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//        _before = s?.subSequence(0, start).toString()
//        _old = s?.subSequence(start, start+count).toString()
//        _after = s?.subSequence(start+count, s.length).toString()
//    }
//
//    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        _new = s?.subSequence(start, start+count).toString()
//    }
//
//    override fun afterTextChanged(s: Editable?) {
//        if(_ignore) return
//        onTextChanged(_before, _old, _new, _after)
//    }
//
//}