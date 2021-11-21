package me.whirledsol.jsoncrypt

import org.json.JSONArray
import org.json.JSONObject

class JsonUtil {

    /**
     * Navigate to node
     */
    fun searchJsonNode(parentObj: JSONObject, searchValue: String): JSONObject?
    {
        for(key in parentObj.keys())
        {
            var value = parentObj.get(key);
            //TODO: JSONArray handling
            if(value is JSONObject){
                var result = searchJsonNode(value as JSONObject,searchValue)
                if(result != null){return result;}
            }
            else if(value is JSONArray){
                var result = searchJsonNode(value as JSONArray,searchValue)
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