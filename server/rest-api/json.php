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
                if ($allschemas->$schema) {
                    $validationErrors = JsonUtils::validateObject($parsed, $allschemas->$schema, $allschemas);
                    if (sizeof($validationErrors) == 0)
                        return true;
                    else
                        return $validationErrors;
                }
                else {
                    $errors[sizeof($errors)] = 'Invalid schema';
                }
            }
            return $errors;
        }

        private static function validateObject($object, $schema, $allschemas) {
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
                if ($definition->required) {
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
            else if ($definition->type == "enumeration" ) {
                $values = $definition->values;
                if (!in_array($value, $values)) {
                    $errors[sizeof($errors)] = "Invalid value for property ´" . $name . "´";
                }
            }
            else if ($definition->type == "fixed") {
                if ($value != $definition->fixedvalue) {
                    $errors[sizeof($errors)] = "Invalid value for property '" . $name . "'";
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
                $itemschema = $allschemas->$itemschema;
            }
            foreach($array as $item) {
                foreach(JsonUtils::validateObject($item, $itemschema, $allschemas) as $error)
                    $errors[sizeof($errors)] = $error;
            }
            return $errors;
        }
        public static function createErrorJson($errors) {
            $result = '{"errors":[';
            $prefix = "";
            foreach($errors as $error) {
                $result .= $prefix . '"' . $error . '"';
                $prefix = ",";
            }
            $result .= "]}";
            return $result;
        }
    }


    /*
    $errors = JsonUtils::validate('{"name":"arjan", "properties": [{"name":"name","type": "string"} ], "rights": [{"grantee": "a", "level": 5}]}', "addclassrequest");
    print_r($errors);
    */
?>