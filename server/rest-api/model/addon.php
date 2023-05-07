<?php
    class Addon {
        public static function validateJson($json, $db) {
            $errors = JsonUtils::validate($json, "addaddonrequest");
            if ($errors && gettype($errors) == "boolean") {
                //Structuur is goed.
                $input = json_decode($json);
                $validClasses = true;
                $classErrors = [];
                foreach($input->classes as $class) {
                    $classErrors = Addon::validateClass($class, $db);
                    foreach($classErrors as $error)
                        $classErrors[sizeof($classErrors)] = $error;
                }
                if (sizeof($classErrors) == 0)
                    return true;
                else {
                    return $classErrors;
                }
            }
            else {
                return $errors;
            }
        }

        private static function validateClass($class, $db) {
            if ($class->security) {
                if (Addon::validateSecurity($class->security, $db))
                    return true;
                else
                    return ["Invalid security for class '" . $class->name . "'"];
            }
            else
                return true;
        }

        private static function validateSecurity($security, $db) {
            $invalid = false;
            $rights = $db->getRights();
            foreach($security as $current) {
                $properties = array_keys(get_object_vars($current));
                if (sizeof($properties) != 2 || !in_array('grantee', $properties) || !in_array('rights', $properties)) {
                    $invalid = true;
                    break;
                }
                else {
                    foreach($current->rights as $right) {
                        $foundRight = false;
                        foreach($rights as $checkright) {
                            if ($checkright->getName() == $right || $checkright->getSystemRight() == $right) {
                                $foundRight = true;
                                break;
                            }
                        }
                        if (!$foundRight) {
                            $invalid = true;
                            break;
                        }
                    }
                }
            }
            return !$invalid;
        }
    }?>