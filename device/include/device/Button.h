#pragma once

class Button{
    public:
        void initialize();
        bool wasClicked();

    private:
        bool previousState = false;
};