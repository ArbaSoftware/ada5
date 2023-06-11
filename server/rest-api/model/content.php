<?php
    class Content {
        private $objectId;
        private $contentFile;
        private $majorVersion;
        private $minorVersion;
        private $fileName;
        private $mimeType;
        private $size;

        public function __construct($objectid, $contentfile, $majorversion, $minorversion, $filename, $mimetype, $size) {
            $this->objectId = $objectid;
            $this->contentFile = $contentfile;
            $this->majorVersion = $majorversion;
            $this->minorVersion = $minorversion;
            $this->fileName = $filename;
            $this->mimeType = $mimetype;
            $this->size = $size;
        }

        public function getObjectId() {
            return $this->objectId;
        }

        public function getContentFile() {
            return $this->contentFile;
        }

        public function getMajorVersion() {
            return $this->majorVersion;
        }

        public function getMinorVersion() {
            return $this->minorVersion;
        }

        public function getFileName() {
            return $this->fileName;
        }

        public function getMimetype() {
            return $this->mimeType;
        }

        public function getSize() {
            return $this->size;
        }
    }