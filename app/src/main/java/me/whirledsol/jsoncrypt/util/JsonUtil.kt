package me.whirledsol.jsoncrypt.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class JsonUtil {

    /**
     * safeCastJsonObject
     */
    fun safeCastJsonObject(jsonStr: String?): JSONObject
    {
        try {
            return JSONObject(jsonStr)
        } catch (ex: JSONException) {
            var wrapper = JSONObject()
            try {
                wrapper.put("array", JSONArray(jsonStr))
                return wrapper;
            } catch (ex1: JSONException) {
                wrapper.put("content", jsonStr ?: "")
            }
            return wrapper
        }
    }

    /**
     * Navigate to node
     */
    fun searchJsonNode(parentObj: JSONObject, searchValue: String): JSONObject?
    {
        for(key in parentObj.keys())
        {
            var value = parentObj.get(key)
            if(value is JSONObject){
                var result = searchJsonNode(value,searchValue)
                if(result != null){return result;}
            }
            else if(value is JSONArray){
                var result = searchJsonNode(value,searchValue)
                if(result != null){return result;}
            }
            else if(value.toString().uppercase().contains(searchValue.uppercase())) {
                return parentObj
            }
        }
        return null
    }

    /**
     * Override to handle array
     */
    private fun searchJsonNode(parentObj: JSONArray, searchValue: String): JSONObject?
    {
        for (i in 0 until parentObj.length()) {
            val item = parentObj.getJSONObject(i)
            var result = searchJsonNode(item,searchValue)
            if(result != null){return result;}
        }
        return null
    }
}