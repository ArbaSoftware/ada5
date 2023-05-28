<?php
    interface Db {
        public function getStores();
        public function getStore($id);
        public function isStoreNameUnique($name);
        public function isUniqueClassName($storeid, $classname);
        public function getIdentifyProviders();
        public function getInternalUser($mail, $passwordhash, $idpid);
        public function canCreateStore();
        public function createStore($name, $grantedrights, $addons);
        public function canCreateClass($storeid);
        public function canDeleteStore($storeid);
        public function createClass($storeid, $class);
        public function areValidRights($rights);
        public function getClasses($storeid);
        public function getClass($storeid, $classid);
        public function getRights();
        public function getIdentityProviders();
        public function deleteStore($storeid);
        public function canAddAddon();
        public function canUpdateAddon();
        public function addAddon($id, $name, $json);
        public function updateAddon($id, $name, $json);
        public function canCreateObject($classid);
        public function createObject($storeid, $class, $request);
        public function getObject($storeid, $objectid);
        public function search($storeid, $search);
    }