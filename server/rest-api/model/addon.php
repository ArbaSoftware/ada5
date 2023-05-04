<?php
    class Addon {
        public static function validateJson($json, $db) {
            $input = json_decode($json);
            if ($input) {
                if (!$input->classes)
                    $input->classes = [];
                if (strlen($input->id) > 0 && strlen($input->name) > 0 && is_array($input->classes)) {
                    $validClasses = true;
                    foreach($input->classes as $class) {
                        if (!Addon::validateClass($class, $db)) {
                            $validClasses = false;
                            break;
                        }
                    }
                    return $validClasses;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        private static function validateClass($class, $db) {
            $allowedproperties = ['name', 'description', 'folderclass', 'documentclass', 'properties', 'security'];
            $propertytypes = ["name"=>"string", "description"=>"string", "folderclass"=>"boolean","documentclass"=>"boolean","properties"=>"array","security"=>"array"];
            $properties = array_keys(get_object_vars($class));
            $invalidProperty= false;
            foreach($properties as $property) {
                if (!in_array($property, $allowedproperties)) {
                    $invalidProperty = true;
                    break;
                }
                else if (gettype($class->$property) != $propertytypes[$property]) {
                    $invalidProperty = true;
                    break;
                }
                else if ($property == 'properties' && !Addon::validateProperties($class->properties)) {
                    $invalidProperty = true;
                    break;
                }
                else if ($property == 'security' && !Addon::validateSecurity($class->security, $db)) {
                    $invalidProperty = true;
                    break;
                }
            }
            return !$invalidProperty;
        }

        private static function validateProperties($properties) {
            $foundInvalid = false;
            foreach($properties as $property) {
                if (!Addon::validateProperty($property)) {
                    $foundInvalid = true;
                    echo $property->name . ' invalid';
                    break;
                }
            }
            return !$foundInvalid;
        }

        private static function validateProperty($property) {
            $allowedproperties = ['name', 'type', 'required', 'multiple'];
            $propertytypes = ["name"=>"string", "type"=>"string", "required"=>"boolean","multiple"=>"boolean"];
            $properties = array_keys(get_object_vars($property));
            $invalidProperty= false;
            foreach($properties as $propertyproperty) {
                if (!in_array($propertyproperty, $allowedproperties)) {
                    $invalidProperty = true;
                    break;
                }
                else if (gettype($property->$propertyproperty) != $propertytypes[$propertyproperty]) {
                    $invalidProperty = true;
                    break;
                }
            }
            return !$invalidProperty;
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