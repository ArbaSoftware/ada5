<?php
    class JsonUtils {
        public static function validate($json, $schema) {
            $errors = [];
            $parsed = json_decode($json);
            if (!$parsed) {
                $errors[sizeof($errors)] = 'Invalid json';
            }
            else {
                $allschemas = json_decode(file_get_contents('schemas.json'));
                if (isset($allschemas->schemas->$schema)) {
                    $validationErrors = JsonUtils::validateObject($parsed, $allschemas->schemas->$schema, $allschemas);
                    if (sizeof($validationErrors) == 0)
                        return true;
                    else
                        return $validationErrors;
                }
                else {
                    $jsonSchema = json_decode($schema);
                    if ($jsonSchema) {
                        $validationErrors = JsonUtils::validateObject($parsed, $jsonSchema, $allschemas);
                        if (sizeof($validationErrors) == 0)
                            return true;
                        else
                            return $validationErrors;
                    }
                    else {
                        $errors[sizeof($errors)] = 'Invalid schema (' . $schema . ')';
                    }
                }
            }
            return $errors;
        }

        private static function validateObject($object, $schema, $allschemas) {
            if (gettype($object) == 'array') {
                debug_print_backtrace();
            }
            $errors = [];
            $objectProperties = get_object_vars($object);
            $schemaProperties = get_object_vars($schema);
            foreach(array_keys($objectProperties) as $property) {
                if (!array_key_exists($property, $schemaProperties))
                    $errors[sizeof($errors)] = "Invalid property '" . $property . "'";
                else {
                    $propertyErrors = JsonUtils::validateProperty($property, $objectProperties[$property], $schemaProperties[$property], $allschemas);
                    foreach($propertyErrors as $error) {
                        $errors[sizeof($errors)] = $error;
                    }
                }
            }
            foreach (array_keys($schemaProperties) as $property) {
                $definition = $schemaProperties[$property];
                if (isset($definition->required) && $definition->required) {
                    if (!array_key_exists($property, $objectProperties)) {
                        $errors[sizeof($errors)] = "Required property '" . $property . "' missing";
                    }
                }
            }
            return $errors;
        }
        private static function validateProperty($name, $value, $definition, $allschemas) {
            $errors = [];
            $propertytype = gettype($value);
            if ($propertytype == 'array' && ($definition->type == 'arrayofstrings' || $definition->type == 'arrayofobjects')) {
                if ($definition->type == 'arrayofstrings') {
                    foreach(JsonUtils::validateArrayOfStrings($name,$value) as $error) 
                        $errors[sizeof($errors)] = $error;
                }
                else if ($definition->type == 'arrayofobjects') {
                    if ($definition->schema) {
                        foreach(JsonUtils::validateArrayOfObjects($name, $value, $definition->schema, $allschemas) as $error)
                            $errors[sizeof($errors)] = $error;
                    }
                    else {
                        $errors[sizeof($errors)] = 'Invalid schema - no schema for array of objects';
                    }
                }
            }
            else if (isset($definition->type) && $definition->type == "string") {
                if ($propertytype != "string")
                    $errors[sizeof($errors)] = 'Invalid value for property ' . $name;
            }
            else if (isset($definition->type) && $definition->type == "enumeration" ) {
                if (gettype($definition->values) == "string") {
                    $enum = $definition->values;
                    $values = $allschemas->enumerations->$enum;
                }
                else {
                    $values = $definition->values;
                }
                if (!in_array($value, $values)) {
                    $errors[sizeof($errors)] = "Invalid value for property ´" . $name . "´";
                }
            }
            else if (isset($definition->type) && $definition->type == "fixed") {
                if ($value != $definition->fixedvalue) {
                    $errors[sizeof($errors)] = "Invalid value for property '" . $name . "'";
                }
            }
            else if (isset($definition->type) && $definition->type == "date") {
                if (gettype($value) == "object") {
                    $validationErrors = JsonUtils::validateObject($value, $allschemas->schemas->date, $allschemas);
                    if (sizeof($validationErrors) == 0) {
                        if ($value->month < 1 or $value->month > 12)
                            $errors[sizeof($errors)] = "Invalid month for property '" . $name . "'";
                        else {
                            $nrofdays = [-1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
                            if((0 == $value->year % 4) & (0 != $value->year % 100) | (0 == $value->year % 400))
                                $nrofdays[2] = 29;
                            if ($value->day < 1 || $value->day > $nrofdays[$value->month])
                                $errors[sizeof($errors)] = "Invalid day for property '". $name . "'";
                            if ($value->year < 0)
                                $errors[sizeof($errors)] = "Invalid year for property '" . $name . "'";
                        }
                    }
                    else {
                        $errors[sizeof($errors)] = "Invalid date value for property '" . $name . "'";
                    }
                }
                else {
                    $errors[sizeof($errors)] = "Invalid date value for property '" . $name . "'";
                }
            }
            else if (isset($definition->type) && $definition->type == "object") {
                if (gettype($value) != "string") {
                    $errors[sizeof($errors)] = "Invalid value for property '" . $name . "'";
                }
            }
            else if (isset($definition->type) && $definition->type == "base64") {
                if (gettype($value) != "string") {
                    $errors[sizeof($errors)] = "Invalid value for property '" . $name . "'";
                }
                else {
                    if (!base64_decode($value, true))
                        $errors[sizeof($errors)] = "Invalid value for property '" . $name . "'";
                }
            }
            else if (isset($definition->type) && $definition->type == "any") { 
                //No validation
            }
            else if (isset($definition->schema)) {
                if (gettype($definition->schema) == "string") {
                    $schemaName = $definition->schema;
                    $objectErrors = JsonUtils::validateObject($value, $allschemas->schemas->$schemaName, $allschemas);
                }
                else {
                    $objectErrors = JsonUtils::validateObject($value, $definition->schema, $allschemas);
                }
                foreach($objectErrors as $error) {
                    $errors[sizeof($errors)] = $error;
                }
            }
            else if ($propertytype != $definition->type)
                $errors[sizeof($errors)] = "Property '" . $name . "' has an invalid type (" . $definition->type . ' <> ' . gettype($value) . ")";
            return $errors;
        }
        private static function validateArrayOfStrings($name, $array) {
            $onlyStrings = true;
            foreach($array as $item) {
                if (gettype($item) != 'string') {
                    $onlyStrings = false;
                    break;
                }
            }
            if ($onlyStrings)
                return [];
            else
                return ["Property '" . $name . "' is not a string array"];
        }

        private static function validateArrayOfObjects($name, $array, $itemschema, $allschemas) {
            $errors = [];
            if (gettype($itemschema) == "string") {
                $itemschema = $allschemas->schemas->$itemschema;
            }
            foreach($array as $item) {
                foreach(JsonUtils::validateObject($item, $itemschema, $allschemas) as $error)
                    $errors[sizeof($errors)] = $error;
            }
            return $errors;
        }
        public static function createErrorJson($errors) {
            try {
                $result = '{"errors":[';
                $prefix = "";
                foreach($errors as $error) {
                    $result .= $prefix . '"' . $error . '"';
                    $prefix = ",";
                }
                $result .= "]}";
                return $result;
            }
            catch (Exception $err) {
                return "[]";
            }
        }
    }
?>