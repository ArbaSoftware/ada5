<?php
    class JsonUtils {
        public static function validate($json, $schema) {
            $parsed = json_decode($json);
            if (!$parsed) {
                return false;
            }
            return true;
        }
    }

    if (JsonUtils::validate('{}', '')) {
        echo 'valid';
    }
    else {
        echo 'invalid';
    }

    
?>